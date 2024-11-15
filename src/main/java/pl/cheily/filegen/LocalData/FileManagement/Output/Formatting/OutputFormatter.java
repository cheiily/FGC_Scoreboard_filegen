package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.List;
import java.util.Map;

/**
 * A formatter is defined by the set of its assigned {@link FormattingUnit}s.
 *  These units present a complete list of accepted inputs and matching expected outputs.
 *  This means that a custom formatter can be easily composed by providing a different list of units, or modifying the existing ones.
 *  That ability however is currently not presented by the GUI in order to simplify the user experience - instead a number of presets is presented in the form of Formatters to choose from.
 *  For runtime customization - the Units themselves can be enabled or disabled.
 */
public interface OutputFormatter {
    String getName();
    List<FormattingUnit> getFormats();

    /**  Asks the formatter to figure out its required inputs on its own by querying the {@link pl.cheily.filegen.LocalData.DataManager} directly.
     *
     * @param resource resource to be obtained
     * @return formatted string to be written into the resource
     */
    String getFormatted(ResourcePath resource);
    Map<ResourcePath, String> getAllFormatted();

}
