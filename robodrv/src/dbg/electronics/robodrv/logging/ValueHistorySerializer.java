package dbg.electronics.robodrv.logging;

import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.io.IoUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 */
public class ValueHistorySerializer {

    private List<ValueWithHistory> values = new ArrayList<ValueWithHistory>();

    private String csvDelimiter = ",";

    public void setValues(List<ValueWithHistory> values) {
        this.values = values;
    }

    class Column {
        final String name;
        Map<Long, Integer> valuesByTime = new LinkedHashMap<Long, Integer>();

        Column(String name, List<TimeSeries> values) {
            this.name = name;
            for (TimeSeries timeSeries : values) {
                valuesByTime.put(timeSeries.time, timeSeries.value);
            }
        }
    }

    class Row {
        final long time;

        Map<String, String> columns;

        Row(long time, Map<String, String> currentData) {
            this.time = time;
            columns  = new LinkedHashMap<String, String>(currentData);
        }

        String toCsvRow(Set<String> colOrder) {
            StringBuilder builder = new StringBuilder();
            builder.append(time).append(csvDelimiter);
            for (String name : colOrder) {
                builder.append(columns.get(name)).append(csvDelimiter);
            }
            builder.append("\n");
            return builder.toString();
        }

    }

    public void save(String snapshotName) throws IOException {

        Map<String, Column> mapByNames = new LinkedHashMap<String, Column>();

        Set<Long> sortedTimes = new TreeSet<Long>();

        Map<String, String> currentData = new LinkedHashMap<String, String>();

        for (ValueWithHistory valueWithHistory : values) {
            Column column = new Column(valueWithHistory.getName(), valueWithHistory.getCurrentSeries());
            mapByNames.put(column.name, column);
            sortedTimes.addAll(column.valuesByTime.keySet());
            currentData.put(column.name, "0");
        }

        List<Row> rows = new ArrayList<Row>();

        for (Long time : sortedTimes) {

             for (String name : mapByNames.keySet()) {
                 if (mapByNames.get(name).valuesByTime.containsKey(time)) {
                     currentData.put(name, String.valueOf(mapByNames.get(name).valuesByTime.get(time)));
                 }
             }

            rows.add(new Row(time, currentData));

        }

        FileWriter writer = new FileWriter(snapshotName + ".csv");

        StringBuilder header = new StringBuilder();

        header.append("time").append(csvDelimiter);

        for (String name : mapByNames.keySet()) {
            header.append(name).append(csvDelimiter);
        }

        header.append("\n");

        writer.write(header.toString());

        for (Row row : rows) {
            writer.write(row.toCsvRow(mapByNames.keySet()));
        }

        writer.flush();

    }



    public void restore(String fileName) throws IOException {
        if (!fileName.endsWith(".csv")) {
            fileName += ".csv";
        }

        List<String> lines = IoUtils.readLines(fileName);

        if (lines.size() == 0) {
            return;
        }

        // remove header line
        lines.remove(0);


        for (String line : lines) {

            String[] columns = line.split(csvDelimiter);

            for (int i=0; i<values.size(); i++) {
                values.get(i).getSnapshot().add(new TimeSeries(Integer.valueOf(columns[0]),Integer.valueOf(columns[i + 1])));
            }

        }


    }


}
