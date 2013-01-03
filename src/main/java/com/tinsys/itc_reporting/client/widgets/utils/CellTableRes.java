package com.tinsys.itc_reporting.client.widgets.utils;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;

public interface CellTableRes extends Resources {
  @Source({ CellTable.Style.DEFAULT_CSS, "cellTable.css" })
  TableStyle cellTableStyle();

  interface TableStyle extends CellTable.Style {
  }

}
