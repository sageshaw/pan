package datastructures;

public interface Exportable {

    String[] header();

    String[][] body();


    default String csvHeader() {
        String[] headers = header();

        String result = "";

        for (String header : headers) {
            result += header + ",";
        }

        return result + System.lineSeparator();
    }

    default String csvBody() {
        String[][] lines = body();

        String result = "";

        for (String[] line : lines) {
            for (String str : line) {
                result += str + ",";
            }

            result += System.lineSeparator();
        }

        return result;
    }

    default String txtHeader() {
        String[] headers = header();

        String result = "";

        for (String header : headers) {
            result += header + "\t";
        }

        return result + System.lineSeparator();
    }

    default String txtBody() {
        String[][] lines = body();

        String result = "";

        for (String[] line : lines) {
            for (String str : line) {
                result += str + "\t";
            }

            result += System.lineSeparator();
        }

        return result;
    }

}
