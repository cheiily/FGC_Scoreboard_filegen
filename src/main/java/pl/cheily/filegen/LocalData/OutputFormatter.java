package pl.cheily.filegen.LocalData;

public interface OutputFormatter {
    /**
     * Formats a resource to the desired layout.<br>
     * It is worth noting how the multipart data is passed:<br>
     * <ul>
     *     <li>In case of player names the elements are passed as follows: <ol>
     *         <li>organization tag - null if none assigned</li>
     *         <li>player name - never null</li>
     *         <li>grand finals entrant bracket side - null if the current round is not GF, otherwise the result of {@link Boolean#toString(boolean)} on <code>true</code> if winner-, or on <code>false</code> if loser-side</li>
     *     </ol></li>
     *     <li>In case of commentary data: (all arguments might be null)<ol>
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
