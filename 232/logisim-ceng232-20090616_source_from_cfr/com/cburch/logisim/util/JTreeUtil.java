/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.JTreeDragController;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JTreeUtil {
    private static final Insets DEFAULT_INSETS = new Insets(20, 20, 20, 20);
    private static final DataFlavor NODE_FLAVOR = new DataFlavor("application/x-java-jvm-local-objectref", "Node");
    private static Object draggedNode;
    private static BufferedImage image;

    public static void configureDragAndDrop(JTree tree, JTreeDragController controller) {
        tree.setAutoscrolls(true);
        new TreeTransferHandler(tree, controller, 3, true);
    }

    private static void autoscroll(JTree tree, Point cursorLocation) {
        Insets insets = DEFAULT_INSETS;
        Rectangle outer = tree.getVisibleRect();
        Rectangle inner = new Rectangle(outer.x + insets.left, outer.y + insets.top, outer.width - (insets.left + insets.right), outer.height - (insets.top + insets.bottom));
        if (!inner.contains(cursorLocation)) {
            Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left, cursorLocation.y - insets.top, insets.left + insets.right, insets.top + insets.bottom);
            tree.scrollRectToVisible(scrollRect);
        }
    }

    static {
        image = null;
    }

    private static class TreeTransferHandler
    implements DragGestureListener,
    DragSourceListener,
    DropTargetListener {
        private JTree tree;
        private JTreeDragController controller;
        private DragSource dragSource;
        private Rectangle rect2D = new Rectangle();
        private boolean drawImage;

        protected TreeTransferHandler(JTree tree, JTreeDragController controller, int action, boolean drawIcon) {
            this.tree = tree;
            this.controller = controller;
            this.drawImage = drawIcon;
            this.dragSource = new DragSource();
            this.dragSource.createDefaultDragGestureRecognizer(tree, action, this);
        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
        }

        @Override
        public final void dragEnter(DragSourceDragEvent dsde) {
            int action = dsde.getDropAction();
            if (action == 1) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
            } else if (action == 2) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }

        @Override
        public final void dragOver(DragSourceDragEvent dsde) {
            int action = dsde.getDropAction();
            if (action == 1) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
            } else if (action == 2) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }

        @Override
        public final void dropActionChanged(DragSourceDragEvent dsde) {
            int action = dsde.getDropAction();
            if (action == 1) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
            } else if (action == 2) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }

        @Override
        public final void dragExit(DragSourceEvent dse) {
            dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }

        @Override
        public final void dragGestureRecognized(DragGestureEvent dge) {
            TreePath path = this.tree.getSelectionPath();
            if (path != null) {
                draggedNode = path.getLastPathComponent();
                if (this.drawImage) {
                    Rectangle pathBounds = this.tree.getPathBounds(path);
                    JComponent lbl = (JComponent)this.tree.getCellRenderer().getTreeCellRendererComponent(this.tree, draggedNode, false, this.tree.isExpanded(path), this.tree.getModel().isLeaf(path.getLastPathComponent()), 0, false);
                    lbl.setBounds(pathBounds);
                    image = new BufferedImage(lbl.getWidth(), lbl.getHeight(), 3);
                    Graphics2D graphics = image.createGraphics();
                    graphics.setComposite(AlphaComposite.getInstance(3, 0.5f));
                    lbl.setOpaque(false);
                    lbl.paint(graphics);
                    graphics.dispose();
                }
                this.dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop, image, new Point(0, 0), new TransferableNode(draggedNode), this);
            }
        }

        @Override
        public final void dragEnter(DropTargetDragEvent dtde) {
            Point pt = dtde.getLocation();
            int action = dtde.getDropAction();
            if (this.drawImage) {
                this.paintImage(pt);
            }
            if (this.controller.canPerformAction(this.tree, draggedNode, action, pt)) {
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public final void dragExit(DropTargetEvent dte) {
            if (this.drawImage) {
                this.clearImage();
            }
        }

        @Override
        public final void dragOver(DropTargetDragEvent dtde) {
            Point pt = dtde.getLocation();
            int action = dtde.getDropAction();
            JTreeUtil.autoscroll(this.tree, pt);
            if (this.drawImage) {
                this.paintImage(pt);
            }
            if (this.controller.canPerformAction(this.tree, draggedNode, action, pt)) {
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public final void dropActionChanged(DropTargetDragEvent dtde) {
            Point pt = dtde.getLocation();
            int action = dtde.getDropAction();
            if (this.drawImage) {
                this.paintImage(pt);
            }
            if (this.controller.canPerformAction(this.tree, draggedNode, action, pt)) {
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public final void drop(DropTargetDropEvent dtde) {
            try {
                if (this.drawImage) {
                    this.clearImage();
                }
                int action = dtde.getDropAction();
                Transferable transferable = dtde.getTransferable();
                Point pt = dtde.getLocation();
                if (transferable.isDataFlavorSupported(NODE_FLAVOR) && this.controller.canPerformAction(this.tree, draggedNode, action, pt)) {
                    Object newParentNode;
                    TreePath pathTarget = this.tree.getPathForLocation(pt.x, pt.y);
                    Object node = transferable.getTransferData(NODE_FLAVOR);
                    if (this.controller.executeDrop(this.tree, node, newParentNode = pathTarget.getLastPathComponent(), action)) {
                        dtde.acceptDrop(action);
                        dtde.dropComplete(true);
                        return;
                    }
                }
                dtde.rejectDrop();
                dtde.dropComplete(false);
            }
            catch (Exception e) {
                dtde.rejectDrop();
                dtde.dropComplete(false);
            }
        }

        private final void paintImage(Point pt) {
            this.tree.paintImmediately(this.rect2D.getBounds());
            this.rect2D.setRect((int)pt.getX(), (int)pt.getY(), image.getWidth(), image.getHeight());
            this.tree.getGraphics().drawImage(image, (int)pt.getX(), (int)pt.getY(), this.tree);
        }

        private final void clearImage() {
            this.tree.paintImmediately(this.rect2D.getBounds());
        }
    }

    private static class TransferableNode
    implements Transferable {
        private Object node;
        private DataFlavor[] flavors = new DataFlavor[]{JTreeUtil.access$000()};

        public TransferableNode(Object nd) {
            this.node = nd;
        }

        @Override
        public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (flavor == NODE_FLAVOR) {
                return this.node;
            }
            throw new UnsupportedFlavorException(flavor);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return this.flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return Arrays.asList(this.flavors).contains(flavor);
        }
    }

}

