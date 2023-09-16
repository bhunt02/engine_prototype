package classes.data_structures;

import classes.data_types.BoundingBox;
import classes.data_types.Pair;
import classes.data_types.Vector2;
import classes.objects.BaseObject;
import classes.utilities.VMath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
public class DynamicAreaTree {
    public class RaycastResult {
        public BaseObject object;
        public Vector2 hit;
        public Vector2 normal;

        double magnitude;
        private RaycastResult(BaseObject object, Vector2 hit, Vector2 normal, double magnitude) {
            this.object = object;
            this.hit = hit;
            this.normal = normal;
            this.magnitude = magnitude;
        }
    }
    public class Node implements Comparable<Node>,PropertyChangeListener {
        public BoundingBox element;
        public BaseObject source;
        public Node parent;
        public Node left;
        public Node right;
        public Node(BoundingBox element, BaseObject obj) {
            this.element = element;
            this.source = obj;
            if (this.source != null) {
                element.addPropertyChangeListener(this);
            }
        }
        public boolean hasRight() {
            return this.right != null;
        }
        public boolean hasLeft() {
            return this.left != null;
        }
        public boolean isLeaf() {
            return (this.right == null && this.left == null);
        }
        public boolean isRoot() {
            return this.parent == null;
        }

        public boolean isUnion() {
            return this.source == null;
        }
        protected void print(StringBuilder buffer, String prefix, String childrenPrefix) {
            LinkedList<Node> children = new LinkedList<>();
            if (this.left != null) children.add(this.left);
            if (this.right != null) children.add(this.right);
            buffer.append(prefix);
            String n = (this.source != null ? this.source.Name : "UNION");
            if (this.isRoot()) n = "ROOT";
            else if (this.parent.left == this) n += "_L";
            else if (this.parent.right == this) n += "_R";
            buffer.append((n));
            buffer.append('\n');
            for (Iterator<Node> it = children.iterator(); it.hasNext();) {
                Node next = it.next();
                if (it.hasNext()) {
                    next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }
        }

        public void removed() {
            element.removePropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            remove(this);
            if (this.source != null) {
                add(this.source);
            } else {
                add(this.element);
            }
        }

        @Override
        public int compareTo(Node o) {
            return 0;
        }
    }

    private class Edge extends Pair<Node,Node> {
        protected Edge(Node n0, Node n1) {
            super(n0,n1);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge)) return false;
            Edge e = (Edge) obj;
            if (e.getValue0() == null || e.getValue1() == null || this.getValue0() == null || this.getValue1() == null) return false;
            return super.equals(e);
        }
    }

    public Node ROOT;

    public void add(BaseObject element) {
        Node n = new Node(element.BB,element);
        insert(n);
    }

    public void add(BoundingBox element) {
        Node n = new Node(element,null);
        insert(n);
    }
    private boolean contains(Node parent, BaseObject src) {
        if (parent == null || parent.source == null) return false;
        return (parent.source.equals(src) || contains(parent.left,src) || contains(parent.right,src));
    }
    private void insert(Node node) {
        if (contains(ROOT,node.source)) return;
        if (ROOT == null) {ROOT = node; return;}

        Node bestSibling = ROOT;
        int bestWeight = computeCost(ROOT,node);
        Queue<Pair<Node, Integer>> NodeQueue = new LinkedList<>();
        NodeQueue.offer(new Pair<>(ROOT,0));

        while (!NodeQueue.isEmpty()) {
            Pair<Node, Integer> next = NodeQueue.poll();
            Node n = next.getValue0();
            int weight = computeCost(n, node);
            int deltaWeight = next.getValue1();

            int totalCost = weight + deltaWeight;
            if (totalCost < bestWeight) {
                bestSibling = n;
                bestWeight = totalCost;
            }

            deltaWeight += weight - n.element.surfaceArea();

            int lowerBoundCost = (int) (node.element.surfaceArea()+deltaWeight);
            if (lowerBoundCost < bestWeight) {
                if (n.hasLeft()) {
                    NodeQueue.offer(new Pair<>(n.left,deltaWeight));
                }
                if (n.hasRight()) {
                    NodeQueue.offer(new Pair<>(n.right,deltaWeight));
                }
            }
        }

        Node oldParent = bestSibling.parent;
        Node newParent = new Node(null,null);
        newParent.parent = oldParent;
        newParent.element = BoundingBox.Union(bestSibling.element,node.element);
        newParent.left = bestSibling;
        newParent.right = node;
        bestSibling.parent = newParent;
        node.parent = newParent;

        if (oldParent != null) {
            if (oldParent.left == bestSibling) oldParent.left = newParent;
            else oldParent.right = newParent;
            refactorInteriorNodes(oldParent,true);
        } else ROOT = newParent;
    }

    private void refactorInteriorNodes(Node start, boolean rotate) {
        while (start != null) {
            BoundingBox leftBox = (start.left != null ? start.left.element : null);
            BoundingBox rightBox = (start.right != null ? start.right.element : null);
            start.element = BoundingBox.Union(leftBox,rightBox);
            if (rotate) Rotate(start);
            start = start.parent;
        }
    }

    public void remove(BoundingBox element) {
        if (element == null) return;
        delete(null,null,element);
    }
    public void remove(Node node) {
        if (node == null) return;
        delete(null,node,null);
    }
    public void remove(BaseObject src) {
        if (src == null) return;
        delete(src,null,null);
    }
    private void delete(BaseObject src, Node node, BoundingBox element) {
        if (ROOT == null) return;
        Node match = (node != null ? node : findNode(src,element));
        if (match != null) {
            match.removed();
            if (!match.isRoot()) {
                Node parent = match.parent;
                Node sibling = (parent.left == match ? parent.right : parent.left);
                if (!parent.isRoot()) {
                    Node ancestor = parent.parent;
                    sibling.parent = ancestor;
                    if (ancestor.left == parent) {
                        ancestor.left = sibling;
                    } else {
                        ancestor.right = sibling;
                    }
                } else {
                    ROOT = sibling;
                    sibling.parent = null;
                }

                Node ancestor = sibling.parent;
                refactorInteriorNodes(ancestor,false);
            } else {
                if (ROOT == match) {
                    ROOT = null;
                }
            }
        }
    }
    private Node findNode(BaseObject src, BoundingBox element) {
        Queue<Node> toSearch = new LinkedList<>();
        toSearch.offer(ROOT);

        Node match = null;
        while (!toSearch.isEmpty()) {
            Node query = toSearch.poll();
            if (query == null) continue;

            if (query.source == src || query.element == element) {
                match = query;
                break;
            }

            if (query.hasLeft()) toSearch.offer(query.left);
            if (query.hasRight()) toSearch.offer(query.right);
        }
        return match;
    }
    protected int weight(Node n) {
        return (int) n.element.surfaceArea();
    }
    private int computeCost(Node n0, Node n1) {
        return (int) BoundingBox.Union(n0.element,n1.element).surfaceArea();
    }
    private void Rotate(Node node) {
        if (node == null || !(node.hasLeft() || node.hasRight())) return;

        Node parent = node.parent;
        if (parent == null) return;
        Node sibling = (parent.left == node ? parent.right : parent.left);
        if (sibling == null || !(sibling.hasLeft() || sibling.hasRight())) return;

        Integer[] costDiffs = new Integer[4];
        Arrays.fill(costDiffs,null);

        if (node.hasLeft()) costDiffs[0] = ((int) BoundingBox.Union(sibling.element, node.left.element).surfaceArea() - weight(node));
        if (node.hasRight()) costDiffs[1] = ((int) BoundingBox.Union(sibling.element, node.right.element).surfaceArea() - weight(node));
        if (sibling.hasLeft()) costDiffs[2] = ((int) BoundingBox.Union(node.element,sibling.left.element).surfaceArea() - weight(sibling));
        if (sibling.hasRight()) costDiffs[3] = ((int) BoundingBox.Union(node.element,sibling.right.element).surfaceArea()- weight(sibling));

        int bestDiffIndex = 0;
        for (int i = 0; i < 4; i++) {
            Integer c = costDiffs[i];
            if (c == null || costDiffs[bestDiffIndex] == null) {
                continue;
            }
            if (c < costDiffs[bestDiffIndex]) {
                bestDiffIndex = i;
            }
        }

        if (costDiffs[bestDiffIndex] != null && costDiffs[bestDiffIndex] < 0) {
            switch (bestDiffIndex) {
                case 0 -> swapRight(node, parent, sibling);
                case 1 -> swapLeft(node, parent, sibling);
                case 2 -> swapRight(sibling, parent, node);
                case 3 -> swapLeft(sibling, parent, node);
            }
        }
    }
    private void swapRight(Node node, Node parent, Node sibling) {
        if (node.right == null) return;
        int nodeWeight = weight(node);
        int potentialWeight = (int) (BoundingBox.Union(sibling.element,node.left.element)).surfaceArea();
        if (potentialWeight > nodeWeight) return;

        if (parent.left == sibling)
            parent.left = node.right;
        else
            parent.right = node.right;
        node.right.parent = parent;
        node.right = sibling;
        sibling.parent = node;
        node.element = BoundingBox.Union(node.right.element,node.left.element);
    }
    private void swapLeft(Node node, Node parent, Node sibling) {
        if (node.left == null) return;
        int nodeWeight = weight(node);
        int potentialWeight = (int) (BoundingBox.Union(sibling.element,node.right.element)).surfaceArea();
        if (potentialWeight > nodeWeight) return;

        if (parent.left == sibling)
            parent.left = node.left;
        else
            parent.right = node.left;
        node.left.parent = parent;
        node.left = sibling;
        sibling.parent = node;
        node.element = BoundingBox.Union(node.left.element,node.right.element);
    }

    public ArrayList<BaseObject> getCollisionPairs(BaseObject obj, ArrayList<BaseObject> ignore) {
        if (ROOT == null) return null;

        ArrayList<BaseObject> res = new ArrayList<>();
        Node node = findNode(obj,obj.BB);
        if (ROOT.hasLeft() && ROOT.hasRight()) {
            checkCollision(node, ROOT.left, res, ignore);
            checkCollision(node, ROOT.right, res, ignore);
        }

        return res;
    }

    public RaycastResult RayCast(Vector2 origin, Vector2 direction) {
        ArrayList<RaycastResult> hits = new ArrayList<>();
        checkRay(ROOT,origin,direction,hits);

        if (hits.size() == 0) return null;
        RaycastResult best = null;
        for (RaycastResult r : hits) {
            if (best == null) {best = r; continue;}
            if (r.magnitude < best.magnitude) best = r;
        }
        return best;
    }

    private void checkRay(Node node, Vector2 origin, Vector2 direction, ArrayList<RaycastResult> hits) {
        Vector2 contact_point = new Vector2();
        Vector2 contact_normal = new Vector2();
        if (checkRayOverlap(origin,direction,node.element,contact_point,contact_normal)) {
            if (node.source != null) {
                hits.add(new RaycastResult(node.source, contact_point, contact_normal, VMath.magnitude(contact_point,origin)));
            } else {
                if (node.hasLeft()) checkRay(node.left,origin,direction,hits);
                if (node.hasRight()) checkRay(node.right,origin,direction,hits);
            }
        }
    }
    private void checkCollision(Node node, Node target, ArrayList<BaseObject> colliders, ArrayList<BaseObject> ignore) {
        if (node == target || (ignore != null && target.source != null && ignore.contains(target.source))) return;

        if (checkBoundOverlap(node.element, target.element)) {
            if (target.source != null) {
                colliders.add(target.source);
            } else {
                if (target.hasLeft()) checkCollision(node,target.left,colliders,ignore);
                if (target.hasRight()) checkCollision(node,target.right,colliders,ignore);
            }
        }
    }
    private boolean checkBoundOverlap(BoundingBox a, BoundingBox b) {
        return ((a.x1 >= b.x0 && b.x1 >= a.x0) && (a.y1 >= b.y0 && b.y1 >= a.y0));
    }

    private boolean checkRayOverlap(Vector2 origin, Vector2 direction, BoundingBox e, Vector2 contact_point, Vector2 contact_normal) {
        Vector2 t_near = new Vector2(
                ((e.x0 - origin.x) / direction.x),
                ((e.y0 - origin.y) / direction.y)
        );

        Vector2 t_far = new Vector2(
                ((e.x1 - origin.x) / direction.x),
                ((e.y1 - origin.y) / direction.y)
        );

        if (t_near.x > t_far.x) {
            double temp = t_near.x;
            t_near.x = t_far.x;
            t_far.x = temp;
        }
        if (t_near.y > t_far.y) {
            double temp = t_near.y;
            t_near.y = t_far.y;
            t_far.y = temp;
        }

        if (t_near.x > t_far.y || t_near.y > t_far.x) return false;

        double t_hit_near = Math.max(t_near.x,t_near.y);
        double t_hit_far = Math.min(t_far.x,t_far.y);

        if (t_hit_far < 0) return false;

        contact_point.x = origin.x + t_hit_near * direction.x;
        contact_point.y = origin.y + t_hit_near * direction.y;

        if (t_near.x > t_near.y)
            if (direction.x < 0) {
                contact_normal.x = 1;
                contact_normal.y = 0;
            } else {
                contact_normal.x = -1;
                contact_normal.y = 0;
            }
        else if (t_near.x < t_near.y)
            if (direction.x < 0) {
                contact_normal.x = 0;
                contact_normal.y = 1;
            } else {
                contact_normal.x = 0;
                contact_normal.y = -1;
            }

        return true;
    }
    private boolean checkPointOverlap(Vector2 point, BoundingBox b) {
        return ((point.x >= b.x0 && point.x <= b.x1) && (point.y >= b.y0 && point.y <= b.y1));
    }

    @Override
    public String toString() {
        if (ROOT == null) return null;
        StringBuilder buffer = new StringBuilder();
        ROOT.print(buffer,"","");
        return buffer.toString();
    }
}

