import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class Graph {
    // Store the adjency items
    private Map<String, List<String>> mapElements = new HashMap<String, List<String>>();
    // Store dependency count
    private Map<String, Integer> dependencies = new HashMap<>();
    // Store installed items
    private Set<String> installedItems = new LinkedHashSet<>();

    /*
     * This method populates adjency info and dependency info
     */
    public void addItems(String[] lineArr) {
        String item = lineArr[1].trim();
        for (int i = 2; i < lineArr.length; i++) {
            String dependent = lineArr[i].trim();
            List<String> list = mapElements.get(item);
            if (null == list) {
                list = new ArrayList<String>();
            }
            list.add(dependent);
            mapElements.put(item, list);

            int count = getDependencyCount(dependent);
            dependencies.put(dependent, count + 1);

            if (null == mapElements.get(dependent)) {
                mapElements.put(dependent, new ArrayList<String>());
            }

        }
    }

    /*
     * This method installs the item and tracks it in installedItems
     */
    private void installItem(String item) {

        // install if not installed
        if (!installedItems.contains(item)) {

            // install dependencies
            List<String> nodes = mapElements.get(item);
            if (nodes != null && nodes.size() > 0) {
                Iterator<String> it = nodes.iterator();
                while (it.hasNext()) {
                    String node = it.next();
                    // install if not installed
                    if (!installedItems.contains(node)) {
                        System.out.println("  Installing " + node);
                        installedItems.add(node);
                    }
                }
            }

            // Install item
            System.out.println("  Installing " + item);
            installedItems.add(item);

        } else {
            System.out.println("  " + item + " is already installed.");
        }
    }

    private int getDependencyCount(String node) {
        return dependencies.get(node) == null ? 0 : dependencies.get(node);
    }

    private void removeItem(String item, boolean isNode) {
        int count = getDependencyCount(item);
        boolean isInstalled = installedItems.contains(item);

        // remove only if installed
        if (isInstalled) {
            // check if its not dependent
            if (count <= 0) {
                System.out.println("  Removing " + item);
                installedItems.remove(item);

                // reduce its dependency on other items
                List<String> nodes = mapElements.get(item);
                if (nodes != null && nodes.size() > 0) {
                    Iterator<String> it = nodes.iterator();
                    while (it.hasNext()) {
                        String node = it.next();
                        dependencies.put(node, getDependencyCount(node) - 1);
                        if (isNode) {
                            removeItem(node, false);
                        }
                    }
                }

            } else if (isNode) {
                System.out.println("  " + item + " is still needed.");
            }
        } else if (isNode) {
            System.out.println("  " + item + " is not installed.");
        }
    }

    /*
     * This method prints the list of installed items in the order they got
     * installed
     */
    private void listInstalled() {
        Iterator<String> it = installedItems.iterator();
        while (it.hasNext()) {
            String key = it.next();
            System.out.println("  " + key);
        }
    }

    /*
     * This method process the input commands and invoke the respective methods
     */
    public void processLine(String line) {
        String[] lineArr = line.split(" ");
        String action = lineArr[0].trim();
        String item = null;
        switch (action) {
            case "DEPEND":
                addItems(lineArr);
                break;
            case "INSTALL":
                item = lineArr[1].trim();
                installItem(item);
                break;
            case "REMOVE":
                item = lineArr[1].trim();
                removeItem(item, true);
                break;
            case "LIST":
                listInstalled();
                break;
        }
    }

    public static void main(String[] args) {
        File file = new File("/Users/mohaa5/SelfLearning/MyJava/input.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(file);

            Graph g = new Graph();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
                g.processLine(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }

    }

}