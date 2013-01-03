package com.tinsys.itc_reporting.client.widgets.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ShuttleBox extends Composite implements HasChangeHandlers {

  interface MyUiBinder extends UiBinder<Widget, ShuttleBox> {
  }

  private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  private boolean isContentChanged = false;
  @UiField(provided = true)
  SortableListBox availableItemsListbox;
  @UiField(provided = true)
  SortableListBox selectedItemsListbox;

  public ShuttleBox() {
    this.availableItemsListbox = new SortableListBox(true);
    this.selectedItemsListbox = new SortableListBox(true);
    this.availableItemsListbox.setWidth("150px");
    this.selectedItemsListbox.setWidth("150px");
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
        isContentChanged = true;
      }
    }
    this.removeItems(this.availableItemsListbox, toBeRemovedItems);
    this.refreshLayout();
    this.selectedItemsListbox.sort();
  }

  @UiHandler("addAllButton")
  void handleAddAllButtonClick(ClickEvent e) {
    List<Integer> toBeRemovedItems = new ArrayList<Integer>();
    for (int i = 0; i < this.availableItemsListbox.getItemCount(); i++) {
      this.selectedItemsListbox.addItem(this.availableItemsListbox.getItemText(i), this.availableItemsListbox.getValue(i));
      toBeRemovedItems.add(i);
      isContentChanged = true;
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
        isContentChanged = true;
      }
    }
    this.removeItems(this.selectedItemsListbox, toBeRemovedItems);
    this.refreshLayout();
    this.availableItemsListbox.sort();
  }

  @UiHandler("removeAllButton")
  void handleRemoveAllButtonClick(ClickEvent e) {
    List<Integer> toBeRemovedItems = new ArrayList<Integer>();
    for (int i = 0; i < this.selectedItemsListbox.getItemCount(); i++) {
      this.availableItemsListbox.addItem(this.selectedItemsListbox.getItemText(i), this.selectedItemsListbox.getValue(i));
      toBeRemovedItems.add(i);
      isContentChanged = true;
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

  public void refreshLayout() {
    int totalItems = this.availableItemsListbox.getItemCount() + this.selectedItemsListbox.getItemCount();
    this.availableItemsListbox.setVisibleItemCount(totalItems);
    this.selectedItemsListbox.setVisibleItemCount(totalItems);
    if (isContentChanged) {
      DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
      isContentChanged = false;
    }
  }

  @Override
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    // TODO Auto-generated method stub
    return addHandler(handler, ChangeEvent.getType());
  }

  public void reset() {
    this.availableItemsListbox.clear();
    this.selectedItemsListbox.clear();
  }

}
