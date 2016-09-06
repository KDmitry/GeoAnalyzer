package edu.dmitry.geomap.swingwaypoint;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointRenderer;

public class WaypointPainter<W extends Waypoint> extends AbstractPainter<JXMapViewer>
{
    private WaypointRenderer<? super W> renderer = new DefaultWaypointRenderer();
    private Set<W> waypoints = new HashSet<W>();

    public WaypointPainter()
    {
        setAntialiasing(true);
        setCacheable(false);
    }

    public void setRenderer(WaypointRenderer<W> r)
    {
        this.renderer = r;
    }

    public Set<W> getWaypoints()
    {
        return Collections.unmodifiableSet(waypoints);
    }

    public void setWaypoints(Set<? extends W> waypoints)
    {
        this.waypoints.clear();
        this.waypoints.addAll(waypoints);
    }

    public void addWaypoint(W waypoint)
    {
        this.waypoints.add(waypoint);
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height)
    {
        if (renderer == null) {
            return;
        }

        Rectangle viewportBounds = map.getViewportBounds();
        g.translate(-viewportBounds.getX(), -viewportBounds.getY());

        for (W w : getWaypoints()) {
            renderer.paintWaypoint(g, map, w);
        }

        g.translate(viewportBounds.getX(), viewportBounds.getY());
    }

}
