/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 * 
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can 
 *  redistribute it and/or modify it under the terms of the 
 *  GNU Affero General Public License as published by the Free Software 
 *  Foundation, either version 3 of the License, or (at your option) any 
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JFrame;

import stream.Context;
import stream.runtime.LifeCycle;

/**
 * @author chris
 * 
 */
public class Dashboard implements DashboardService, LifeCycle {

    protected JFrame frame;

    protected String title = "";
    protected Integer width = 1024;
    protected Integer height = 768;

    final Map<String, JComponent> components = new HashMap<String, JComponent>();

    /**
     * @see stream.service.Service#reset()
     */
    @Override
    public void reset() throws Exception {
        if (frame != null) {
            frame.getContentPane().removeAll();
        }
    }

    /**
     * @see stream.service.DashboardService#addWidget(java.lang.String,
     *      javax.swing.JComponent)
     */
    @Override
    public String addWidget(String id, JComponent widget) {

        String wid = id;
        if (wid == null)
            wid = UUID.randomUUID().toString().toUpperCase();

        if (frame != null) {

            if (components.get(wid) != null) {
                JComponent old = components.remove(wid);
                frame.getContentPane().remove(old);
            }

            frame.getContentPane().add(widget);
            components.put(wid, widget);
            return wid;
        } else
            return null;
    }

    /**
     * @see stream.runtime.LifeCycle#init(stream.Context)
     */
    @Override
    public void init(Context context) throws Exception {
        frame = new JFrame();

        if (title != null) {
            frame.setTitle(title);
        }

        if (width != null && height != null)
            frame.setSize(width, height);

        frame.setVisible(true);
    }

    /**
     * @see stream.runtime.LifeCycle#finish()
     */
    @Override
    public void finish() throws Exception {
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }
}