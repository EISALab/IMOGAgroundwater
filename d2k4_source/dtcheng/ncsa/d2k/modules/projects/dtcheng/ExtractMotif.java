package ncsa.d2k.modules.projects.dtcheng;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Extracts motif sequences from protein sequences for a certain motif number.
 *
 * @author ashirk
 *
 */
public class ExtractMotif {

  private static final String MOTIF = "PS00211";

  private static final String INTERPRO_URL =
    "jdbc:oracle:thin:@luna.ncsa.uiuc.edu:1521:lunanew";
  private static final String INTERPRO_USER = "interpro_adm";
  private static final String INTERPRO_PASS = "Numb3r5";
  private static final String[] PROTEOME_CONSTRAINT =
    { "HUMAN", "AERPE", "ARATH", "ECOL6", "PYRHO", "DEIRA" };
  private static final String INTERPRO_PHYSEQ_QUERY =
    "SELECT seqtext FROM physicalseq WHERE physeqid = ?";
  private static final String INTERPRO_TOTAL_PROT_QUERY =
    "SELECT COUNT(protein_ac) FROM protein2genome WHERE oscode = ?";
  private static final String PRINTS_DB_CODE = "F";

  private static Connection con = null;
  private static PreparedStatement stmt = null;
  private static ResultSet rs = null;
  static {
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (java.lang.ClassNotFoundException e) {
      e.printStackTrace();
    }
  }


  public static void main(String args[]) {

    log("Running");

    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (java.lang.ClassNotFoundException e) {
      e.printStackTrace();
    }

    try {
      List motifList = collectMotifs(MOTIF, PROTEOME_CONSTRAINT);
      log(toClustalFormat(motifList));
      log(toProteinSummary(motifList));
      log(toCountsDisplay(motifList));
      log(toSequenceDisplay(motifList));

    } catch (Exception e) {
      e.printStackTrace();
    }

    log("Done");

  }

  /*
   * Extracts motifs from protein sequences based on a motif accession number
   * and a proteome constraint.
   *
   * @param motif Motif accession number
   * @param proteomes Array of proteomes to extract the motifs from
   * @return List of ProSeqOrg instances, sorted by protein accession number
   */
  public static List collectMotifs(String motif, String[] proteomes) {
    Map matchMap = getMatchMap(motif, proteomes);
    List motifList = getMotifList2(matchMap);
    Collections.sort(motifList);
    return motifList;
  }

  /*
   * Returns full protein sequences based on a motif accession number
   * and a proteome constraint.
   *
   * @param motif Motif accession number
   * @param proteomes Array of proteomes to extract the motifs from
   * @return List of ProSeqOrg instances, sorted by protein accession number
   */
  public static List collectSequences(String motif, String[] proteomes) {
    Map matchMap = getMatchMap(motif, proteomes);
    List sequenceList = getSequenceList(matchMap);
    Collections.sort(sequenceList);
    return sequenceList;
  }

  /*
   * Constructs an SQL query string for fetching motif/protein matches
   *
   * @param osCodeArray Array of InterPro organism codes
   * @return SQL query string
   */
  private static String getInterproMatchQuery(String[] osCodeArray) {
    StringBuffer constraint = new StringBuffer(1024);
    int size = osCodeArray.length;
    for (int i = 0; i < size; i++) {
      constraint.append("'");
      constraint.append(osCodeArray[i]);
      constraint.append("'");
      if (i < size - 1)
        constraint.append(",");
    }
    String query =
      "SELECT match.protein_ac, dbentry.entry_name, description.text, bioseq.physeq, "
        + "match.pos_from, match.pos_to, match.dbcode, protein2genome.oscode, "
        + "organism.name, organism.full_name FROM "
        + "match, bioseq, dbentry, description, protein2genome, organism "
        + "WHERE match.protein_ac = bioseq.seq_accid AND "
        + "match.protein_ac = protein2genome.protein_ac AND "
        + "protein2genome.oscode = organism.oscode AND "
        + "bioseq.seqid = dbentry.bioseqid AND "
        + "dbentry.dbentryid = description.dbentryid AND "
        + "protein2genome.oscode IN ("
        + constraint.toString()
        + ") AND "
        + "method_ac = ? AND status = 'T' ORDER BY protein_ac, pos_from";
    return query;
  }

  /*
   * Constructs a HashMap of matches keyed by protein accession number.
   * The value at each element is a list of match records.
   *
   * @return Map of matches keyed by protein accession number
   */
  private static Map getMatchMap(String motif, String[] proteomes) {
    Map protMap = new HashMap(1024);
    try {

      // Interpro connection
      con =
        DriverManager.getConnection(INTERPRO_URL, INTERPRO_USER, INTERPRO_PASS);
      stmt = con.prepareStatement(getInterproMatchQuery(proteomes));
      stmt.setString(1, motif);
      rs = stmt.executeQuery();

      String currProt = null;
      String nextProt = null;
      List matches = null;
      Match match = null;
      while (rs.next()) {
        String proteinAc = rs.getString(1);
        // First iteration
        if (currProt == null) {
          currProt = proteinAc;
          match = getMatchInstance(rs);
          matches = new ArrayList(5);
          matches.add(match);
          protMap.put(currProt, matches);
          continue;
        }
        nextProt = proteinAc;
        // Another match for the same protein,
        // so add the additional matches
        if (nextProt.equals(currProt)) {
          match = getMatchInstance(rs);
          matches.add(match);
          currProt = nextProt;
        } else { // New protein and matches
          currProt = proteinAc;
          match = getMatchInstance(rs);
          matches = new ArrayList(5);
          matches.add(match);
          protMap.put(currProt, matches);
        }

      }
      closeResources();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      throw new RuntimeException(sqle.getMessage());
    }
    return protMap;
  }

  /*
   * Fetches all motifs based on the provided match map. For PRINTS motifs,
   * the min,max method is used to derive complete motif.
   *
   * @param matchMap Map of matches keyed by proteins
   * @return List of  instances
   */
  private static List getMotifList(Map matchMap) {

    List motifList = new ArrayList(1024);

    if (matchMap.size() == 0)
      return motifList;

    try {

      // Interpro connection
      con =
        DriverManager.getConnection(INTERPRO_URL, INTERPRO_USER, INTERPRO_PASS);
      stmt = con.prepareStatement(INTERPRO_PHYSEQ_QUERY);

      Iterator protIter = matchMap.keySet().iterator();
      while (protIter.hasNext()) {
        String prot = (String)protIter.next();
        // Get the list of matches for this protein
        List protMatches = (List)matchMap.get(prot);

        String db = ((Match)protMatches.get(0)).getDbCode();
        // Only used for PRINTS matches
        int minPos = -1, maxPos = -1;
        // Get the physical sequence the matches occur in
        Match match = (Match)protMatches.get(0);
        stmt.setInt(1, match.getPhyseq());
        rs = stmt.executeQuery();
        rs.next();
        Clob seqClob = rs.getClob(1);

        int numMatches = protMatches.size();
        // Iterate through all the matches for this protein
        for (int i = 0; i < numMatches; i++) {
          match = (Match)protMatches.get(i);
          if (!PRINTS_DB_CODE.equals(db)) { // If any motif type except PRINTS
            motifList.add(
              new ProSeqOrg(
                prot,
                match.getProteinEntryName(),
                match.getProteinDescription(),
                match.getOsCode(),
                match.getOrgName(),
                match.getOrgFullName(),
                seqClob.getSubString(
                  match.getPosFrom(),
                  match.getPosTo() - match.getPosFrom())));
          } else { // Else this is a PRINTS motif (multi-segment)
            // First match
            if (minPos == -1) {
              minPos = match.getPosFrom();
              maxPos = match.getPosTo();
              continue;
            }
            if (match.getPosFrom() < minPos) {
              minPos = match.getPosFrom();
            }
            if (match.getPosTo() > maxPos) {
              maxPos = match.getPosTo();
            }
            // If this is the last match
            if (i == numMatches - 1) {
              motifList.add(
                new ProSeqOrg(
                  prot,
                  match.getProteinEntryName(),
                  match.getProteinDescription(),
                  match.getOsCode(),
                  match.getOrgName(),
                  match.getOrgFullName(),
                  seqClob.getSubString(minPos, maxPos - minPos)));
              // Reset
              minPos = -1;
              maxPos = -1;
            }
          }
        }
      }

      closeResources();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      throw new RuntimeException(sqle.getMessage());
    }

    return motifList;
  }

  /*
   * Fetches all motifs based on the provided match map. For PRINTS motifs,
   * the concatenation method is used to derive complete motif.
   *
   * @param matchMap Map of matches keyed by proteins
   * @return List of  instances
   */
  private static List getMotifList2(Map matchMap) {
    List motifList = new ArrayList(1024);

    if (matchMap.size() == 0)
      return motifList;

    try {

      // Interpro connection
      con =
        DriverManager.getConnection(INTERPRO_URL, INTERPRO_USER, INTERPRO_PASS);
      stmt = con.prepareStatement(INTERPRO_PHYSEQ_QUERY);

      Iterator protIter = matchMap.keySet().iterator();
      while (protIter.hasNext()) {
        String prot = (String)protIter.next();
        // Get the list of matches for this protein
        List protMatches = (List)matchMap.get(prot);

        String db = ((Match)protMatches.get(0)).getDbCode();
        // Only used for PRINTS matches
        StringBuffer print = new StringBuffer(1024);
        // Get the physical sequence the matches occur in
        Match match = (Match)protMatches.get(0);
        stmt.setInt(1, match.getPhyseq());
        rs = stmt.executeQuery();
        rs.next();
        Clob seqClob = rs.getClob(1);

        int numMatches = protMatches.size();
        // Iterate through all the matches for this protein
        for (int i = 0; i < numMatches; i++) {
          match = (Match)protMatches.get(i);
          if (!PRINTS_DB_CODE.equals(db)) { // If any motif type except PRINTS
            motifList.add(
              new ProSeqOrg(
                prot,
                match.getProteinEntryName(),
                match.getProteinDescription(),
                match.getOsCode(),
                match.getOrgName(),
                match.getOrgFullName(),
                seqClob.getSubString(
                  match.getPosFrom(),
                  match.getPosTo() - match.getPosFrom())));
          } else { // Else this is a PRINTS motif (multi-segment)
            print.append(
              seqClob.getSubString(
                match.getPosFrom(),
                match.getPosTo() - match.getPosFrom()));
            // If this is the last match
            if (i == numMatches - 1) {
              motifList.add(
                new ProSeqOrg(
                  prot,
                  match.getProteinEntryName(),
                  match.getProteinDescription(),
                  match.getOsCode(),
                  match.getOrgName(),
                  match.getOrgFullName(),
                  print.toString()));
              // Reset
              print = new StringBuffer(1024);
            }
          }
        }
      }

      closeResources();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      throw new RuntimeException(sqle.getMessage());
    }

    return motifList;
  }

  /*
   * Fetches full protein sequences based on the provided match map.
   *
   * @param matchMap Map of matches keyed by proteins
   * @return List of ProSeqOrg instances
   */
  private static List getSequenceList(Map matchMap) {

    List seqList = new ArrayList(1024);

    if (matchMap.size() == 0)
      return seqList;

    try {

      // Interpro connection
      con =
        DriverManager.getConnection(INTERPRO_URL, INTERPRO_USER, INTERPRO_PASS);
      stmt = con.prepareStatement(INTERPRO_PHYSEQ_QUERY);

      Iterator protIter = matchMap.keySet().iterator();
      while (protIter.hasNext()) {
        String prot = (String)protIter.next();
        // Get the list of matches for this protein
        List protMatches = (List)matchMap.get(prot);
        // Get the physical sequence the matches occur in
        Match match = (Match)protMatches.get(0);
        stmt.setInt(1, match.getPhyseq());
        rs = stmt.executeQuery();
        rs.next();
        Clob seqClob = rs.getClob(1);
        String seq =
          seqClob.getSubString(
            new Long(1).longValue(),
            new Long(seqClob.length()).intValue());
        seqList.add(
          new ProSeqOrg(
            prot,
            match.getProteinEntryName(),
            match.getProteinDescription(),
            match.getOsCode(),
            match.getOrgName(),
            match.getOrgFullName(),
            seq));
      }
      closeResources();
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      throw new RuntimeException(sqle.getMessage());
    }

    return seqList;
  }

  /*
   * Returns a list of HitStats instances containing count
   * stats for the passed in list of ProSeqOrg instances
   *
   * @param sequences List of ProSeqOrg instances
   * @return List of HitStats instances
   */
  private static List getCounts(List sequences){

    List counts = new ArrayList(150);
    Map orgMap = new HashMap(150);

    try {
      // Interpro connection
      con =
        DriverManager.getConnection(INTERPRO_URL, INTERPRO_USER, INTERPRO_PASS);
    } catch (SQLException sqle) {
      sqle.printStackTrace();
      throw new RuntimeException(sqle.getMessage());
    }

    HitStats stats;
    int numSeq = sequences.size();
    for(int i=0;i<numSeq;i++){
      ProSeqOrg pro = (ProSeqOrg)sequences.get(i);
      if(orgMap.containsKey(pro.getOsCode())){
        stats = (HitStats)orgMap.get(pro.getOsCode());
        stats.addHit(pro.getProteinAc());
      } else {
        stats = new HitStats(pro.getOrgFullName());
        orgMap.put(pro.getOsCode(),stats);
        try {
          stmt = con.prepareStatement(INTERPRO_TOTAL_PROT_QUERY);
          stmt.setString(1, pro.getOsCode());
          rs = stmt.executeQuery();
          rs.next();
          stats.setTotalProts(rs.getInt(1));
          stats.addHit(pro.getProteinAc());
        } catch (SQLException sqle) {
          sqle.printStackTrace();
          throw new RuntimeException(sqle.getMessage());
        }
      }
    }
    closeResources();
    counts.addAll(orgMap.values());
    return counts;
  }
  /*
   * Formats a List of ProOrgSeq instances in the format ClustalW
   * requires as input.
   *
   * @param sequences List of ProOrgSeq instances
   * @return Formatted String to be used as ClustalW input
   */
  public static String toClustalFormat(List sequences) {
    String idChar = ".";
    StringBuffer formatted = new StringBuffer(32768);
    int size = sequences.size();
    String currProt = null;
    String nextProt = null;
    int id = 1;
    for (int i = 0; i < size; i++) {
      nextProt = ((ProSeqOrg)sequences.get(i)).getProteinAc();
      if (nextProt.equals(currProt)) {
        id++;
      } else {
        id = 1;
        currProt = nextProt;
      }
      formatted.append(">");
      formatted.append(((ProSeqOrg)sequences.get(i)).getProteinEntryName());
      // Look forward to see if this protein name occurs again
      if (id == 1 && i < size - 1) {
        String futureProt = ((ProSeqOrg)sequences.get(i + 1)).getProteinAc();
        if (currProt.equals(futureProt)) {
          // Name occurs again, let's give it a unique id
          formatted.append(idChar + String.valueOf(id));
        }
      }
      // Id has been incremented, so let's append it to the protein name
      if (id != 1) {
        formatted.append(idChar + String.valueOf(id));
      }
      formatted.append("\n");
      formatted.append(((ProSeqOrg)sequences.get(i)).getSequence());
      formatted.append("\n");
    }
    return formatted.toString();
  }

  /*
   * Formats a List of ProOrgSeq instances in a table
   * with a summary of the protein information, including
   * protein accession number, entry name, full name
   * (description), and the organism in which it is found.
   *
   * @param sequences List of ProOrgSeq instances
   * @return Formatted String summarizing protein info
   */
  public static String toProteinSummary(List sequences) {

    if (sequences.size() == 0)
      return "";

    String idChar = ".";
    StringBuffer formatted = new StringBuffer(32768);
    int size = sequences.size();
    String currProt = null;
    String nextProt = null;
//    formatted.append("<div class=\"resultHeading\">Protein Hits</div>\n");
    formatted.append(
      "<table border=\"0\" cellspacing=\"0\" cellpadding=\"5\">\n");
    formatted.append("<tr>");
    formatted.append(
      "<td class=\"result\">Accession</td>"
        + "<td class=\"result\">Entry Name</td>"
        + "<td class=\"result\">Full Name</td>"
        + "<td class=\"result\">Organism</td>");
    formatted.append("</tr>\n");
    boolean doId = false;
    int id = 1;
    for (int i = 0; i < size; i++) {
      ProSeqOrg proInfo = (ProSeqOrg)sequences.get(i);
      nextProt = proInfo.getProteinAc();
      if (nextProt.equals(currProt)) {
        id++;
      } else {
        id = 1;
        currProt = nextProt;
      }
      formatted.append("<tr>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      // Link the accession number to SwissProt
      formatted.append(
        "<a class=\"resultLink\" target=\"_blank\" href=\"http://"
          + "us.expasy.org/cgi-bin/niceprot.pl?"
          + nextProt
          + "\">");
      formatted.append(nextProt).append("</a>");
      formatted.append("</td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      // Link the entry name to the full sequence
      formatted.append("<a class=\"resultLink\" href=\"#");
      formatted.append(proInfo.getProteinEntryName());
      // Look forward to see if this protein occurs again
      if (id == 1 && i < size - 1) {
        String futureProt = ((ProSeqOrg)sequences.get(i + 1)).getProteinAc();
        if (currProt.equals(futureProt)) {
          // Name occurs again, let's give it a unique id
          formatted.append(idChar + String.valueOf(id));
          doId = true;
        }
      }
      // Id has been incremented, so let's append it to the protein
      if (id != 1) {
        formatted.append(idChar + String.valueOf(id));
        doId = true;
      }
      formatted.append("\">");
      formatted.append(proInfo.getProteinEntryName());
      if(doId) {
        formatted.append(idChar + String.valueOf(id));
        doId = false;
      }
      formatted.append("</a></td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(proInfo.getProteinDescription()).append("</td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(proInfo.getOrgFullName()).append("</td>");
      formatted.append("<tr>\n");
    }
    formatted.append("</table>\n");
    return formatted.toString();
  }

  /*
   * Produces a table of hit statistics given the passed in
   * list of ProOrgSeq instances.
   *
   * @param sequences List of ProOrgSeq instances
   * @return Formatted String containing hit statistics
   */
  public static String toCountsDisplay(List sequences) {
    if (sequences.size() == 0)
      return "";

    DecimalFormat percentFormatter = new DecimalFormat("##0.00000%");
    DecimalFormat relativeFormatter = new DecimalFormat("##0.000000");
    List hitStats = getCounts(sequences);
    Collections.sort(hitStats);

    StringBuffer formatted = new StringBuffer(4096);
//    formatted.append("<div class=\"resultHeading\">Hit Statistics</div>\n");
    formatted.append(
      "<table border=\"0\" cellspacing=\"0\" cellpadding=\"5\">\n");
    formatted.append("<tr>");
    formatted.append(
      "<td class=\"result\">Organism</td>"
        + "<td class=\"result\">Number of Hits</td>"
        + "<td class=\"result\">Percent of Proteome</td>"
        + "<td class=\"result\">Relative Occurance x100</td>");
    formatted.append("</tr>\n");

    HitStats stats;
    int statsSize = hitStats.size();
    for(int i=0;i<statsSize;i++){
      stats = (HitStats)hitStats.get(i);
      formatted.append("<tr>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(stats.getOrgFullName()).append("</td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(stats.getTotalHits()).append("</td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(percentFormatter.format(stats.getHitPercent())).append("</td>");
      formatted.append("<td class=\"result\" valign=\"top\">");
      formatted.append(relativeFormatter.format(stats.getRelativeOccurance())).append("</td>");
      formatted.append("</tr>\n");
    }
    formatted.append("</table>");
    return formatted.toString();
  }

  /*
   * Returns a list of sequences along with their respective
   * accession numbers and entry names.
   *
   * @param sequences List of ProOrgSeq instances
   * @return Formatted String containing protein sequences
   */
  public static String toSequenceDisplay(List sequences) {
    if (sequences.size() == 0) return "";

    String idChar = ".";
    StringBuffer formatted = new StringBuffer(32768);
    int size = sequences.size();
    String currProt = null;
    String nextProt = null;
//    formatted.append("<div class=\"resultHeading\">Sequences</div>\n");
    formatted.append("<div class=\"result\">\n");
    boolean doId = false;
    int id = 1;
    for (int i = 0; i < size; i++) {
      nextProt = ((ProSeqOrg)sequences.get(i)).getProteinAc();
      if (nextProt.equals(currProt)) {
        id++;
      } else {
        id = 1;
        currProt = nextProt;
      }
      formatted.append(">");
      formatted.append("<a name=\"");
      formatted.append(((ProSeqOrg)sequences.get(i)).getProteinEntryName());
      // Look forward to see if this protein name occurs again
      if (id == 1 && i < size - 1) {
        String futureProt = ((ProSeqOrg)sequences.get(i + 1)).getProteinAc();
        if (currProt.equals(futureProt)) {
          // Name occurs again, let's give it a unique id
          formatted.append(idChar + String.valueOf(id));
          doId = true;
        }
      }
      // Id has been incremented, so let's append it to the protein name
      if (id != 1) {
        formatted.append(idChar + String.valueOf(id));
        doId = true;
      }
      formatted.append("\">");
      formatted.append(((ProSeqOrg)sequences.get(i)).getProteinEntryName());
      if(doId) {
        formatted.append(idChar + String.valueOf(id));
        doId = false;
      }
      formatted.append("</a>");

      formatted.append("<br><br>");
      formatted.append("\n");
      formatted.append(addSpaces(((ProSeqOrg)sequences.get(i)).getSequence()));
      formatted.append("<br><br>");
      formatted.append("\n");
    }
    return formatted.toString();
  }

  /*
   * Inserts a space into a string every 10 characters.
   *
   * @param str String to add spaces to
   * @return String with spaces added
   */
  private static String addSpaces(String str) {
    int chunkSize=10;
    int length=str.length();
    if(chunkSize>length)return str;
    StringBuffer formatted = new StringBuffer(4096);
    int i=0;
    for(;i+chunkSize-1<length;i+=chunkSize){
     formatted.append(str.substring(i,i+chunkSize)).append(" ");
    }
    formatted.append(str.substring(i));
    return formatted.toString();
  }

  private static void printMotifList(List l) {
    for (int i = 0; i < l.size(); i++) {
      System.out.println(
        ((ProSeqOrg)l.get(i)).getOsCode()
          + " - "
          + ((ProSeqOrg)l.get(i)).getProteinAc()
          + " - "
          + ((ProSeqOrg)l.get(i)).getSequence());
    }
  }
  private static Match getMatchInstance(ResultSet rs) {
    try {
      return new Match(
        rs.getString(1),
        rs.getString(2),
        rs.getString(3),
        rs.getInt(4),
        rs.getInt(5),
        rs.getInt(6),
        rs.getString(7),
        rs.getString(8),
        rs.getString(9),
        rs.getString(10));
    } catch (SQLException sqle) {
      throw new RuntimeException(sqle);
    }
  }

  public static class Match {
    private String proteinAc;
    private String proteinEntryName;
    private String proteinDescription;
    private int physeq;
    private int posFrom;
    private int posTo;
    private String dbCode;
    private String osCode;
    private String orgName;
    private String orgFullName;
    public Match(
      String proteinAc,
      String proteinEntryName,
      String proteinDescription,
      int physeq,
      int posFrom,
      int posTo,
      String dbCode,
      String osCode,
      String orgName,
      String orgFullName) {
      this.proteinAc = proteinAc;
      this.proteinEntryName = proteinEntryName;
      this.proteinDescription = proteinDescription;
      this.physeq = physeq;
      this.posFrom = posFrom;
      this.posTo = posTo;
      this.dbCode = dbCode;
      this.osCode = osCode;
      this.orgName = orgName;
      this.orgFullName = orgFullName;
    }
    public String getProteinAc() {
      return proteinAc;
    }
    public String getProteinEntryName() {
      return proteinEntryName;
    }
    public String getProteinDescription() {
      return proteinDescription;
    }
    public int getPhyseq() {
      return physeq;
    }
    public int getPosFrom() {
      return posFrom;
    }
    public int getPosTo() {
      return posTo;
    }
    public String getDbCode() {
      return dbCode;
    }
    public String getOsCode() {
      return osCode;
    }
    public String getOrgName() {
      return orgName;
    }
    public String getOrgFullName() {
      return orgFullName;
    }
  }

  public static class ProSeqOrg implements Comparable {

    private String proteinAc;
    private String proteinEntryName;
    private String proteinDescription;
    private String osCode;
    private String orgName;
    private String orgFullName;
    private String sequence;
    public ProSeqOrg(
      String protein,
      String proteinEntryName,
      String proteinDescription,
      String osCode,
      String orgName,
      String orgFullName,
      String sequence) {
      this.proteinAc = protein;
      this.proteinEntryName = proteinEntryName;
      this.proteinDescription = proteinDescription;
      this.osCode = osCode;
      this.orgName = orgName;
      this.orgFullName = orgFullName;
      this.sequence = sequence;
    }
    public String getProteinAc() {
      return proteinAc;
    }
    public String getProteinEntryName() {
      return proteinEntryName;
    }
    public String getProteinDescription() {
      return proteinDescription;
    }
    public String getOsCode() {
      return osCode;
    }
    public String getOrgName() {
      return orgName;
    }
    public String getOrgFullName() {
      return orgFullName;
    }
    public String getSequence() {
      return sequence;
    }
    public int compareTo(Object o) {
      return proteinAc.compareTo(((ProSeqOrg)o).getProteinAc());
    }
  }

  public static class HitStats implements Comparable {
    /** Full name of the organism */
    String orgFullName;
    /** Set of all the proteins at least one hit has occured in */
    Set protSet;
    /** Proteins in the organism */
    int totalProts;
    /** Motif hits in the organism */
    int totalHits;

    public HitStats(String orgFullName) {
      totalProts = 0;
      totalHits = 0;
      this.protSet = new java.util.HashSet(100);
      this.orgFullName = orgFullName;
    }
    public String getOrgFullName() {
      return orgFullName;
    }
    public int getTotalProts() {
      return totalProts;
    }
    public int getTotalHits() {
      return totalHits;
    }

    public double getHitPercent() {
      return (double)protSet.size()/(double)totalProts;
    }

    public double getRelativeOccurance() {
      return ((double)totalHits / (double)totalProts)*100;
    }

    public void setTotalProts(int totalProts){
      this.totalProts = totalProts;
    }

    public void addHit(String proteinAc){
      totalHits++;
      protSet.add(proteinAc);
    }

    public int compareTo(Object o) {
      return orgFullName.compareTo(((HitStats)o).getOrgFullName());
    }

  }


  /*
   * Closes database resources.
   */
  private static void closeResources() {
    try {
      rs.close();
      stmt.close();
      con.close();
      rs = null;
      stmt = null;
      con = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void log(String msg) {
    System.out.println(msg);
  }
}
