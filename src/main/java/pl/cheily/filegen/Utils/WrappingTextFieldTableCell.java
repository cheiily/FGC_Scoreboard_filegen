package pl.cheily.filegen.Utils;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class WrappingTextFieldTableCell {
    public static <T> javafx.util.Callback<TableColumn<T, String>, javafx.scene.control.TableCell<T, String>> forTableColumn() {
        return col -> {
            TableCell<T, String> cell = new TableCell<>();
            Text text = new Text();
            text.wrappingWidthProperty().bind(col.widthProperty());
            text.setTextAlignment(TextAlignment.CENTER);
            text.textProperty().bind(cell.itemProperty());
            text.fillProperty().bind(cell.textFillProperty());
            cell.setAlignment(Pos.CENTER);
            cell.setGraphic(text);
            cell.setPrefHeight(Region.USE_COMPUTED_SIZE);
            return cell;
        };
    }
}