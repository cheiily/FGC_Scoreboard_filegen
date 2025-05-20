package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import org.junit.jupiter.api.Test;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
class FormattingUnitFactoryTest {

    // Replaces placeholders in customInterpolationFormat with corresponding values from params array
    @Test
    public void customInterpolateFmt_replaces_placeholders_with_values() {
        // Arrange
        FormattingUnit unit = new FormattingUnit(
                true,
                Arrays.asList(MatchDataKey.P1_NAME, MatchDataKey.P2_NAME),
                null,
                "Sample output",
                null,
                "Player 1: {P1_NAME} vs Player 2: {P2_NAME}",
                FormattingUnitMethodReference.CUSTOM_INTERPOLATION
        );
        String[] params = {"John", "Alice"};

        // Act
        String result = FormattingUnitFactory.customInterpolateFmt(unit, params);

        // Assert
        assertEquals("Player 1: John vs Player 2: Alice", result);
    }

    // Handles missing parameters in params array
    @Test
    public void customInterpolateFmt_missing_parameters() {
        // Arrange
        FormattingUnit unit = new FormattingUnit(
                true,
                Arrays.asList(MatchDataKey.P1_NAME, MatchDataKey.P2_NAME),
                null,
                "Sample output",
                null,
                "Player 1: {P1_NAME} vs Player 2: {P2_NAME}",
                FormattingUnitMethodReference.CUSTOM_INTERPOLATION
        );
        String[] params = {"John"};

        // Act
        String result = FormattingUnitFactory.customInterpolateFmt(unit, params);

        // Assert
        assertEquals("Player 1: John vs Player 2: {P2_NAME}", result);
    }

    // Handles extra parameters in params array
    @Test
    public void customInterpolateFmt_extra_parameters() {
        // Arrange
        FormattingUnit unit = new FormattingUnit(
                true,
                Arrays.asList(MatchDataKey.P1_NAME, MatchDataKey.P2_NAME),
                null,
                "Sample output",
                null,
                "Player 1: {P1_NAME} vs Player 2: {P2_NAME}",
                FormattingUnitMethodReference.CUSTOM_INTERPOLATION
        );
        String[] params = {"John", "Alice", "ExtraParam"};

        // Act
        String result = FormattingUnitFactory.customInterpolateFmt(unit, params);

        // Assert
        assertEquals("Player 1: John vs Player 2: Alice", result);
    }

    // Treats lowercase placeholders as case-insensitive
    @Test
    public void customInterpolateFmt_case_insensitive_placeholders() {
        // Arrange
        FormattingUnit unit = new FormattingUnit(
                true,
                Arrays.asList(MatchDataKey.P1_NAME, MatchDataKey.P2_NAME),
                null,
                "Sample output",
                null,
                "Player 1: {p1_name} vs Player 2: {P2_NAME}",
                FormattingUnitMethodReference.CUSTOM_INTERPOLATION
        );
        String[] params = {"John", "Alice"};

        // Act
        String result = FormattingUnitFactory.customInterpolateFmt(unit, params);

        // Assert
        assertEquals("Player 1: John vs Player 2: Alice", result);
    }

    // Handles empty customInterpolationFormat string
    @Test
    public void customInterpolateFmt_handles_empty_format_string() {
        // Arrange
        FormattingUnit unit = new FormattingUnit(
                true,
                Arrays.asList(MatchDataKey.P1_NAME, MatchDataKey.P2_NAME),
                null,
                "Sample output",
                null,
                "",
                FormattingUnitMethodReference.CUSTOM_INTERPOLATION
        );
        String[] params = {"John", "Alice"};

        // Act
        String result = FormattingUnitFactory.customInterpolateFmt(unit, params);

        // Assert
        assertEquals("", result);
    }

}