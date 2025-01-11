package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

public enum FormattingUnitMethodReference {
    ONE_TO_ONE_PASS(true),
    FIND_FLAG_FILE(true),
    CUSTOM_INTERPOLATION(true),

    DEFAULT_FORMAT_P1_NAME(false),
    DEFAULT_FORMAT_P2_NAME(false),
    DEFAULT_FORMAT_COMM_NAME(false),
    FSPR_FORMAT_PLAYER_LOSER_INDICATOR(false);

    public boolean isGuiOption;
    FormattingUnitMethodReference(boolean isGuiOption) {
        this.isGuiOption = isGuiOption;
    }
}
