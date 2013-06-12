package ncsa.d2k.modules.projects.dtcheng.primitive;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ByteStringTreeOfIndices
  {
  // int max_byte_string_length;               // for space & time optimization
  // boolean fixed_byte_string_lengthh = true; // for space & time optimization
  // int key_byte_delta;                       // for converting key bytes to ints

  int     ints_per_node;

  byte    min_byte = Byte.MIN_VALUE;
  byte    max_byte = Byte.MAX_VALUE;

  int     max_num_nodes;
  int     max_node_memory_size;
  int     node_memory[];
  int     node_ints[];

  int     num_nodes      = 0;
  int     next_free_node = 0;


  public ByteStringTreeOfIndices(int max_num_nodes, byte min_byte, byte max_byte)
    {

    this.max_num_nodes = max_num_nodes;
    this.min_byte      = min_byte;
    this.max_byte      = max_byte;

    ints_per_node = (int) max_byte - (int) min_byte + 1;

    max_node_memory_size = max_num_nodes * ints_per_node;


    //System.out.println("ints_per_node = " + ints_per_node);


    allocateMemory();

    allocateNode();

    }


  void allocateMemory()
   {
   node_memory  = new int[max_node_memory_size];
   node_ints    = new int[max_num_nodes];
   }


  void allocateNode()
   {

   int node_memory_start_i =  num_nodes * ints_per_node;
   int node_memory_end_i   =  node_memory_start_i + ints_per_node;

   for (int i = node_memory_start_i; i < node_memory_end_i; i++)
     {
     node_memory[i] = -1;
     }

   node_ints[num_nodes] = -1;

   num_nodes++;
   }


  public void put(byte byte_string[], int start_i, int end_i)
    {
    put(0, byte_string, start_i, end_i, 1);
    }

  public void put(byte byte_string[], int start_i, int end_i, int object_index)
    {
    put(0, byte_string, start_i, end_i, object_index);
    }


  public void put(int node_index, byte byte_string[], int start_i, int end_i, int object_index)
    {

    if (start_i == end_i)
      {
      node_ints[node_index] = object_index;
      return;
      }


    int node_memory_start_i = node_index * ints_per_node;
    int next_node = node_memory[node_memory_start_i + byte_string[start_i] - min_byte];
    if (next_node == -1)
      {
      allocateNode();
      next_node = num_nodes - 1;
      node_memory[node_memory_start_i + byte_string[start_i] - min_byte] = next_node;
      }

    put(next_node, byte_string, start_i + 1, end_i, object_index);

    }


  public boolean contains(byte byte_string[], int start_i, int end_i)
    {
    int index = get(0, byte_string, start_i, end_i);

    if (index == -1)
      return false;
    else
      return true;
    }

  public int get(byte byte_string[])
    {
    return get(0, byte_string, 0, byte_string.length);
    }

  public int get(byte byte_string[], int start_i, int end_i)
    {
    return get(0, byte_string, start_i, end_i);
    }

  public int get(int node_index, byte byte_string[], int start_i, int end_i)
    {
    if (start_i == end_i)
      {
      return node_ints[node_index];
      }

    int node_memory_start_i = node_index * ints_per_node;
    int next_node = node_memory[node_memory_start_i + byte_string[start_i] - min_byte];
    if (next_node == -1)
      {
      return -1;
      }

    return get(next_node, byte_string, start_i + 1, end_i);
    }
  }


