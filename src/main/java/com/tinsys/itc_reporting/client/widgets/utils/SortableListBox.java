package com.tinsys.itc_reporting.client.widgets.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

public class SortableListBox extends ListBox {

  public SortableListBox() {
    super();
  }

  public SortableListBox(boolean isMultipleSelect) {
    super(isMultipleSelect);
  }

  public void sort() {
    List<ObjectToBeSorted> sortTable = new ArrayList<ObjectToBeSorted>();
    for (int i = 0; i < this.getItemCount(); i++) {
      sortTable.add(new ObjectToBeSorted(this.getItemText(i), this.getValue(i)));
    }
    Collections.sort(sortTable);
    this.clear();
    for (int i = 0; i < sortTable.size(); i++) {
      this.addItem(sortTable.get(i).valueTobeSorted, sortTable.get(i).objectId);
    }
  }

  public class ObjectToBeSorted implements Comparable<ObjectToBeSorted> {
    private String valueTobeSorted;
    private String objectId;

    public ObjectToBeSorted(String valueTobeSorted, String objectId) {
      this.valueTobeSorted = valueTobeSorted;
      this.objectId = objectId;
    }

    @Override
    public int compareTo(ObjectToBeSorted o) {

      return this.valueTobeSorted.compareToIgnoreCase(o.valueTobeSorted);
    }
  }
}
