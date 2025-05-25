package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class FormattingUnitBuilder {
    public BooleanProperty enabled = new SimpleBooleanProperty();
    public SimpleListProperty<MatchDataKey> inputKeys = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<MatchDataKey>()));
    public ObjectProperty<ResourcePath> destination = new SimpleObjectProperty<>();
    public StringProperty sampleOutput = new SimpleStringProperty();
    public ObjectProperty<FormattingUnitMethodReference> formatType = new SimpleObjectProperty<>();
    public StringProperty customInterpolationFormat = new SimpleStringProperty();

    public static StringConverter<FormattingUnitMethodReference> methodReferenceStringConverter = new StringConverter<>() {
        @Override
        public String toString(FormattingUnitMethodReference object) {
            return object.toString();
        }

        @Override
        public FormattingUnitMethodReference fromString(String string) {
            return FormattingUnitMethodReference.valueOf(string);
        }
    };

    public static StringConverter<MatchDataKey> matchDataKeyStringConverter = new StringConverter<>() {
        @Override
        public String toString(MatchDataKey object) {
            return object.toString();
        }

        @Override
        public MatchDataKey fromString(String string) {
            return MatchDataKey.fromString(string);
        }
    };

    public static StringConverter<ResourcePath> resourcePathStringConverter = new StringConverter<>() {
        @Override
        public String toString(ResourcePath object) {
            return object.toString();
        }

        @Override
        public ResourcePath fromString(String string) {
            return ResourcePath.fromString(string);
        }
    };

    public FormattingUnitBuilder() {

    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public ListProperty<MatchDataKey> getInputKeys() {
        return inputKeys;
    }

    public void setInputKeys(List<MatchDataKey> inputKeys) {
        this.inputKeys.setAll(inputKeys);
    }

    public ListProperty<MatchDataKey> inputKeysProperty() {
        return inputKeys;
    }

    public ResourcePath getDestination() {
        return destination.get();
    }

    public void setDestination(ResourcePath destination) {
        this.destination.set(destination);
    }

    public ObjectProperty<ResourcePath> destinationProperty() {
        return destination;
    }

    public String getSampleOutput() {
        return sampleOutput.get();
    }

    public void setSampleOutput(String sampleOutput) {
        this.sampleOutput.set(sampleOutput);
    }

    public StringProperty sampleOutputProperty() {
        return sampleOutput;
    }

    public FormattingUnitMethodReference getFormatType() {
        return formatType.get();
    }

    public void setFormatType(FormattingUnitMethodReference formatType) {
        this.formatType.set(formatType);
    }

    public ObjectProperty<FormattingUnitMethodReference> formatTypeProperty() {
        return formatType;
    }

    public String getCustomInterpolationFormat() {
        return customInterpolationFormat.get();
    }

    public void setCustomInterpolationFormat(String customInterpolationFormat) {
        this.customInterpolationFormat.set(customInterpolationFormat);
    }

    public StringProperty customInterpolationFormatProperty() {
        return customInterpolationFormat;
    }

    public boolean validate() {
        boolean pass = !inputKeys.get().isEmpty() && destination.get() != null && formatType.get() != null;

        if (formatType.get() != FormattingUnitMethodReference.CUSTOM_INTERPOLATION)
            return pass;

        Matcher matcher = FormattingUnitFactory.customInterpolationPattern.matcher(customInterpolationFormat.get());
        while (matcher.find()) {
            String s = matcher.group();
            try {
                MatchDataKey val = MatchDataKey.fromString(s.substring(1, s.length() - 1));
                if (!inputKeys.get().contains(val)) {
                    pass = false;
                    break;
                }
            } catch (IllegalArgumentException ex) {
                pass = false;
                break;
            }
        }

        return pass;
    }

    public FormattingUnit build() {
        return FormattingUnit.deserialize(enabled.get(), inputKeys.get(), destination.get(), sampleOutput.get(), customInterpolationFormat.get(), formatType.get());
    }

    public String toString() {
        return "FormattingUnitBuilder{" +
                "enabled=" + enabled.get() +
                ", inputKeys=[" + inputKeys.get().stream().map(MatchDataKey::toString).collect(Collectors.joining()) + "]" +
                ", destination=" + (destination.get() != null ? destination.get().toString() : "null") +
                ", formatType=" + (formatType.get() != null ? formatType.get().toString() : "null") +
                ", customInterpolationFormat='" + customInterpolationFormat.get() + '\'' +
                ", sampleOutput='" + sampleOutput.get() +
                '}';
    }

    public static FormattingUnitBuilder from(FormattingUnit unit) {
        var builder = new FormattingUnitBuilder();
        builder.setEnabled(unit.enabled);
        builder.setInputKeys(unit.inputKeys);
        builder.setDestination(unit.destination);
        builder.setSampleOutput(unit.sampleOutput);
        builder.setFormatType(unit.formatType);
        builder.setCustomInterpolationFormat(unit.customInterpolationFormat);
        return builder;
    }
}
