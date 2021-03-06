package amtrackservice.client.gui.elements;

import java.util.HashMap;

import amtrackservice.client.MapList;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;


public class AmList extends AmWidget {
	
	ListDataProvider<Double> storedDataProvider;
	
	@UiField(provided = true)
	public CellTable<Double> cellTable;
	
	private Widget widget;

	/**
	 * Add the columns to the table.
	 */
	private void initTableColumns( final SelectionModel<Double> selectionModel ) {

		// Values
		Column<Double, String> valuesColumn = new Column<Double, String>( new EditTextCell()) {
			public String getValue(Double object) {
				if( !object.isNaN() )
					return object.toString();
				return "";
			}
		};

		cellTable.addColumn(valuesColumn);
		valuesColumn.setFieldUpdater(new FieldUpdater<Double, String>() {
			public void update(int index, Double object, String value) {
				storedDataProvider.getList().remove(object);
				try{
					object = Double.parseDouble(value);
				} catch (NumberFormatException e){
					System.out.println("Problem: " + e.getMessage());
					object = 0.0;
				}
				storedDataProvider.getList().add(object);
				storedDataProvider.refresh();
			}
		});
	}
	
	
	interface Binder extends UiBinder<Widget, AmList> {}
	
	private String data;
	private MapList<String, String> preset;

	public AmList(String label, String datatype, HTML description,
			MapList<String, String> preset, String dataX) {
		super(label, datatype, description);
		
		storedDataProvider = new ListDataProvider<Double>();
				
		data = dataX;
		this.preset = preset;
		
		this.cellTable = new CellTable<Double>();
		this.cellTable.setWidth("100%");		
		
		// Add a selection model so we can select cells.
		final SelectionModel<Double> selectionModel = new MultiSelectionModel<Double>( );
		cellTable.setSelectionModel(selectionModel);
		initTableColumns(selectionModel);
		
		storedDataProvider.addDataDisplay(cellTable);
		
		Binder uiBinder = GWT.create(Binder.class);
		
		this.widget = uiBinder.createAndBindUi(this);

		this.setDefault();
	}

	@Override
	public String getValue() {
		String s = "";
		for (Double d : storedDataProvider.getList()) {
			if( d != Double.NaN)
				s += " " + d.toString();
		}
		return s;
	}

	@Override
	public Widget getWidget() {
		return this.widget;
	}

	@Override
	public int setValue(HashMap<String, String> valueMap) {
		storedDataProvider.getList().clear();
		return this.appendValue(valueMap);
	}

	@Override
	public String getDataLink() {
		return data;
	}

	@Override
	public void setDefault() {
		storedDataProvider.getList().clear();
		if( this.preset != null ){
			for (String s : preset.getKeys()) {
				storedDataProvider.getList().add(Double.valueOf(preset.getValue(s)));
			}
		}
	}

	@Override
	public int appendValue(HashMap<String, String> valueMap) {
		String str = valueMap.get(data);
		if (str != null) {
			String[] s = str.split(" ");
			for (String c : s) {
				if( !c.equalsIgnoreCase("nan")){
					Double d = Double.valueOf(c);
					storedDataProvider.getList().add(d);
				}
			}
			return 0;
		}
		return -1;
	}

}
