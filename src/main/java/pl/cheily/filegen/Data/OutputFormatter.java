package pl.cheily.filegen.Data;

public interface OutputFormatter {
    /**
     * Formats a resource to the desired layout.<br/>
     * It is worth noting how the multipart data is passed by default:<br/>
     * <ul>
     *     <li>In case of player names, in the following order: <ol>
     *         <li>organization tag - empty string if none assigned</li>
     *         <li>player name - empty during initial/clear save, never expected to be empty</li>
     *         <li>grand finals entrant bracket side - null if the current round is not GF, otherwise the result of
     *              {@link Boolean#toString(boolean)} on <code>true</code> if winner-, or on <code>false</code> if loser-side.<br/>
     *              This is the only argument that is ever expected to be null during normal operation.</li>
     *     </ol></li>
     *     <li>In case of commentary data, in the following order: (arguments will be empty strings if there is no corresponding value)<ol>
     *         <li>host name</li>
     *         <li>arbitrary number of commentator names, currently the default implementation only supports up to two commentators</li>
     *     </ol></li>
     * </ul>
     *
     * @param resourceName identifier of the resource, to decide how the data should be formatted
     * @param data         to format, may consist of multiple separate parts to merge
     * @return formatted data
     */
    String format(String resourceName, String... data);
}
