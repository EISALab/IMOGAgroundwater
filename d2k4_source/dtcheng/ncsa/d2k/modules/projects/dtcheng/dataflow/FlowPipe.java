package ncsa.d2k.modules.projects.dtcheng.dataflow;

import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class FlowPipe
  {
  String    name;
  Object [] objects;
  int       pushIndex;
  int       pullIndex;
  int       numObjects;
  int       capacity;

  FlowPipe(int capacity, String name)
    {
    this.name = name;
    this.objects = new Object[capacity];
    this.numObjects = 0;
    this.capacity = capacity;
    }

  public void push(Object object) throws Exception
    {
    if (numObjects == capacity) {
      System.out.println("push NumObjects == capacity");
      throw new Exception();
    }

    objects[pushIndex] = object;
    pushIndex++;
    if (pushIndex == capacity)
      pushIndex = 0;
    numObjects++;
    }

  public Object pull() throws Exception
    {
    if (numObjects == 0) {
      System.out.println("pull numObjects == 0");
      throw new Exception();
    }

    Object object = objects[pullIndex];
    pullIndex++;
    if (pullIndex == capacity)
      pullIndex = 0;
    numObjects--;
    return object;
    }
  }
