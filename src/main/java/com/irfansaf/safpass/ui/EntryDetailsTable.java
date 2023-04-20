package com.irfansaf.safpass.ui;

import com.irfansaf.safpass.ui.action.TableListener;
import com.irfansaf.safpass.util.Configuration;
import com.irfansaf.safpass.util.DateUtils;
import com.irfansaf.safpass.xml.bind.Entry;

import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;



/**
 * Table to display entry details.
 */
public class EntryDetailsTable extends JTable {

    private static final DateTimeFormatter FORMATTER
            = DateUtils.createFormatter(Configuration.getInstance().get("date.format", "yyyy-MM-dd"));

    private enum DetailType {
        TITLE("Title", Entry::getTitle),
        URL("URL", Entry::getUrl),
        USER("User", Entry::getUser),
        MODIFIED("Modified", entry -> DateUtils.formatIsoDateTime(entry.getLastModification(), FORMATTER)),
        CREATED("Created", entry -> DateUtils.formatIsoDateTime(entry.getCreationDate(), FORMATTER));

        private final String description;
        private final Function<Entry, String> valueMapper;

        DetailType(String description, Function<Entry, String> valueMapper) {
            this.description = description;
            this.valueMapper = valueMapper;
        }

        public String getDescription() {
            return description;
        }

        public String getValue(Entry entry) {
            return entry != null ? valueMapper.apply(entry) : "";
        }
    }

    private static final Map<String, DetailType> DETAILS_BY_NAME = Arrays.stream(DetailType.values())
            .collect(Collectors.toMap(detail -> detail.name(), Function.identity()));

    private static final String[] DEFAULT_DETAILS = {
            DetailType.TITLE.name(),
            DetailType.MODIFIED.name()
    };

    private final List<DetailType> detailsToDisplay;
    private final DefaultTableModel tableModel;

    public EntryDetailsTable() {
        super();

        detailsToDisplay = Arrays.stream(Configuration.getInstance().getArray("entry.details", DEFAULT_DETAILS))
                .map(name -> DETAILS_BY_NAME.get(name))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (detailsToDisplay.isEmpty()) {
            Arrays.stream(DEFAULT_DETAILS)
                    .map(name -> DETAILS_BY_NAME.get(name))
                    .forEach(detailsToDisplay::add);
        }

        tableModel = new DefaultTableModel();
        detailsToDisplay.forEach(detail -> tableModel.addColumn(detail.getDescription()));
        setModel(tableModel);
        getTableHeader().setReorderingAllowed(false);
        addMouseListener(new TableListener());
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if (column > 0) {
            int rendererWidth = component.getPreferredSize().width;
            TableColumn tableColumn = getColumnModel().getColumn(column);
            int columnWidth = Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth());
            tableColumn.setPreferredWidth(columnWidth);
            tableColumn.setMaxWidth(columnWidth);
        }
        return component;
    }

    public void clear() {
        tableModel.setRowCount(0);
    }

    public void addRow(Entry entry) {
        tableModel.addRow(detailsToDisplay.stream()
                .map(detail -> detail.getValue(entry))
                .toArray(Object[]::new));
    }

    public int rowCount() {
        return tableModel.getRowCount();
    }
}
