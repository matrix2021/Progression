package joshie.progression.api.filters;

import java.util.List;

import joshie.progression.api.IFilter.FilterType;
import joshie.progression.gui.newversion.overlays.DrawFeatureHelper;

public interface IFilterSelectorFilter {
    /** Returns the filter type **/
    public FilterType getType();
    
    /** Whether this object is acceptable **/
    public boolean isAcceptable(Object object);

    /** Returns a list of all valid objects in this filter **/
    public List getAllItems();

    /** Whether the search feature matches this object **/
    public boolean searchMatches(Object object, String lowerCase);

    /** Draw the filter objects **/
    public void draw(DrawFeatureHelper helper, Object stack, int offsetX, int j, int yOffset, int k, int mouseX, int mouseY);

    /** Mouse clicked on filter objects **/
    public boolean mouseClicked(int mouseX, int mouseY, int j, int k);

    /** Width of screen **/
    public double getScale();

    /** Gap between items **/
    public int getChange();
}
