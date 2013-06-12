package ncsa.d2k.modules.projects.dtcheng.primitive;
import ncsa.d2k.modules.projects.dtcheng.*;


public class ByteStringTreeOfIndicesV2
  {
  // int max_byte_string_length;               // for space & time optimization
  // boolean fixed_byte_string_lengthh = true; // for space & time optimization
  // int key_byte_delta;                       // for converting key bytes to ints

  int     ints_per_node;

  int     max_num_unique_bytes;
  int     byte_index[];
  int     num_unique_bytes;

  int     max_num_nodes;
  int     max_node_memory_size;
  int     node_memory[];
  int     node_ints[];

  int     num_nodes      = 0;
  int     next_free_node = 0;


  public ByteStringTreeOfIndicesV2(int max_num_nodes, int max_num_unique_bytes)
    {

    this.max_num_nodes = max_num_nodes;

    ints_per_node = max_num_unique_bytes;

    max_node_memory_size = max_num_nodes * ints_per_node;


    allocateMemory();

    allocateNode();

    }


  void allocateMemory()
   {
   node_memory  = new int[max_node_memory_size];
   node_ints    = new int[max_num_nodes];
   byte_index   = new int[256];
   for (int i = 0; i < 256; i++)
     {
     byte_index[i] = -1;
     }
   num_unique_bytes = 0;
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


  public void put(byte byte_string[], int start_i, int end_i) throws Exception
    {
    put(0, byte_string, start_i, end_i, 1);
    }

  public void put(byte byte_string[], int start_i, int end_i, int object_index) throws Exception
    {
    put(0, byte_string, start_i, end_i, object_index);
    }


  public void put(int node_index, byte byte_string[], int start_i, int end_i, int object_index) throws Exception
    {

    if (start_i == end_i)
      {
      node_ints[node_index] = object_index;
      return;
      }


    int node_memory_start_i = node_index * ints_per_node;

    byte x = byte_string[start_i];
    if (byte_index[x] == -1)
      {
      byte_index[x] = num_unique_bytes++;
      //System.out.println("num_unique_bytes = " + num_unique_bytes);
      if (num_unique_bytes > ints_per_node)
        {
        System.out.println("Error!!!  num_unique_bytes > ints_per_node");
        throw new Exception();
        }
      }

    int next_node = node_memory[node_memory_start_i + byte_index[x]];
    if (next_node == -1)
      {
      allocateNode();
      next_node = num_nodes - 1;
      node_memory[node_memory_start_i + byte_index[x]] = next_node;
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

    // check for invalid byte
    byte x = byte_string[start_i];
    if (byte_index[x] == -1)
      {
      return -1;
      }

    int next_node = node_memory[node_memory_start_i + byte_index[x]];
    if (next_node == -1)
      {
      return -1;
      }

    return get(next_node, byte_string, start_i + 1, end_i);
    }
  }


