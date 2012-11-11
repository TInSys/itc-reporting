package com.tinsys.itc_reporting.client.widgets.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ShuttleBox extends Composite {

  interface MyUiBinder extends UiBinder<Widget, ShuttleBox> {
  }
  private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField(provided = true)
  SortableListBox availableItemsListbox;
  @UiField(provided = true)
  SortableListBox selectedItemsListbox;

  public ShuttleBox() {
    this.availableItemsListbox = new SortableListBox(true);
    this.selectedItemsListbox = new SortableListBox(true);
    initWidget(uiBinder.createAndBindUi(this));
  }

  public SortableListBox getAvailableItemsListbox() {
    return availableItemsListbox;
  }

  public void setAvailableItemsListbox(SortableListBox availableItemsListbox) {
    this.availableItemsListbox.clear();
    for (int i = 0; i < availableItemsListbox.getItemCount(); i++) {  
        this.availableItemsListbox.addItem(availableItemsListbox.getItemText(i), availableItemsListbox.getValue(i));
    }
    this.refreshLayout();
    this.availableItemsListbox.sort();
  }

  public SortableListBox getSelectedItemsListbox() {
    return selectedItemsListbox;
  }

  public void setSelectedItemsListbox(SortableListBox selectedItemsListbox) {
    this.selectedItemsListbox.clear();
    for (int i = 0; i < selectedItemsListbox.getItemCount(); i++) {  
        this.selectedItemsListbox.addItem(selectedItemsListbox.getItemText(i), selectedItemsListbox.getValue(i));
    }
    this.refreshLayout();
    this.selectedItemsListbox.sort();
  }

  @UiHandler("addButton")
  void handleAddButtonClick(ClickEvent e) {
    List<Integer> toBeRemovedItems = new ArrayList<Integer>();
    for (int i = 0; i < this.availableItemsListbox.getItemCount(); i++) {
      if (this.availableItemsListbox.isItemSelected(i)) {
        this.selectedItemsListbox.addItem(this.availableItemsListbox.getItemText(i), this.availableItemsListbox.getValue(i));
        toBeRemovedItems.add(i);
      }
    }
    this.removeItems(this.availableItemsListbox, toBeRemovedItems);
    this.refreshLayout();
    this.selectedItemsListbox.sort();
  }

  @UiHandler("removeButton")
  void handleRemoveButtonClick(ClickEvent e) {
    List<Integer> toBeRemovedItems = new ArrayList<Integer>();
    for (int i = 0; i < this.selectedItemsListbox.getItemCount(); i++) {
      if (this.selectedItemsListbox.isItemSelected(i)) {
        this.availableItemsListbox.addItem(this.selectedItemsListbox.getItemText(i), this.selectedItemsListbox.getValue(i));
        toBeRemovedItems.add(i);
      }
    }
    this.removeItems(this.selectedItemsListbox, toBeRemovedItems);
    this.refreshLayout();
    this.availableItemsListbox.sort();
  }

  private void removeItems(ListBox listbox, List<Integer> toBeRemovedItems) {
    Collections.sort(toBeRemovedItems, Collections.reverseOrder());
    for (Integer entryToRemove : toBeRemovedItems) {
      listbox.removeItem(entryToRemove);
    }
  }

  private void refreshLayout() {
    this.availableItemsListbox.setVisibleItemCount(this.availableItemsListbox.getItemCount());
    this.selectedItemsListbox.setVisibleItemCount(this.selectedItemsListbox.getItemCount());
  }

}
