/*
Author: Seongyun Lim
Email: slim2020@my.fit.edu
Course: CSE2010
Section: Section 03
Description of this file: Display user events and timeline using skiplist.
*/

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

// Contents class store a user event and its time
class Contents {
    private String time; // timeline
    private String event; // user event

    // constructor
    public Contents (String time, String event) {
        this.time = time;
        this.event = event;
    }

    // getter methods
    public String getTime(){ return this.time;}
    public String getEvent() {return this.event;}
}

// Node class contains the Contents as an element, and pointers to above, below, previous, and next nodes.
class Node {

    // An element and pointers to other nodes.
    private Contents content;
    private Node above;
    private Node below;
    private Node prev;
    private Node next;

    // constructor
    public Node(Contents content, Node above, Node below, Node prev, Node next) {
        this.content = content;
        this.above = above;
        this.below = below;
        this.prev = prev;
        this.next = next;
    }

    // getter methods
    public Contents getContents() {
        return this.content;
    }
    public Node getAbove() {
        return this.above;
    }
    public Node getBelow() {
        return this.below;
    }
    public Node getNext() {
        return this.next;
    }
    public Node getPrev() {
        return this.prev;
    }

    // setter methods
    public void setAbove(Node above) {
        this.above = above;
    }
    public void setBelow(Node below) {
        this.below = below;
    }
    public void setPrev(Node prev) {
        this.prev = prev;
    }
    public void setNext(Node next) {
        this.next = next;
    }

    // check if the node's pointers are pointint to other nodes.
    public boolean hasAbove() {
        return this.above != null;
    }
    public boolean hasBelow() {
        return this.below != null;
    }
    public boolean hasNext() {
        return this.next != null;
    }
}

// DoublyLinkedList class construct the doubly linked list including header and trailer.
class DoublyLinkedList {

    // header and trailer node
    private Node header;
    private Node trailer;

    // constructor
    public DoublyLinkedList(){
        this.header = new Node(new Contents("0", null), null, null, null, null);
        this.trailer = new Node(new Contents("9999", null), null, null, header, null);
        header.setNext(trailer); // linking header and trailer
    }

    // getter methods
    public Node getHeader(){
        return this.header;
    }
    public Node getTrailer(){
        return this.trailer;
    }
}

// SkipList class builds the skip list using doublyLinkedList.
class SkipList {

    private DoublyLinkedList top; // store the topmost doublyLinkedList in the skipList.
    private int height; // current skipList's height; start from 0
    private FakeRandHeight rand = new FakeRandHeight(); // random number generator; used at each add call

    // constructor
    public SkipList () {
        this.top = new DoublyLinkedList();
        this.height = 0;
    }

    // getter methods
    public int getHeight(){
        return this.height;
    }
    public DoublyLinkedList getTop() {
        return this.top;
    }

    // add new list; used when random height for adding new events is greater or equal to current height
    // linking the current top's header and trailer to new list's heard and trailer and update the top
    private void addList(DoublyLinkedList list) {
        Node firstTop = top.getHeader(); // current top's header
        Node lastTop = top.getTrailer(); // current top's trailer
        Node firstList = list.getHeader(); // new top's header
        Node lastList = list.getTrailer(); // new top's trailer
        firstTop.setAbove(firstList); // current top's above pointer to new top's header
        firstList.setBelow(firstTop); // new top's header's below pointer to current top's header
        lastTop.setAbove(lastList); // current top's trailer's above pointer to new top's trailer
        lastList.setBelow(lastTop); // new top's trailer's below to current top's trailer
        top = list; // update skipList's top
    }

    // delete the top list; used when there is more than one empty list on the top after removing events.
    private void deleteList() {
        Node start = this.top.getHeader(); // start from the top list
        start = start.getBelow(); // move down since the top list shoud be an empty list
        boolean empty = true; // indicate current height is an empty list or not.
        // delete the list if it is not null and an empty list.
        while (start != null && empty) {
            // start is the header node and if header.next is trailer, then it is an empty list.
            if (start.getNext().getContents().getTime().equals("9999")) {
                // change the above and below pointer of the above and below list to delete current list.
                start.getAbove().setBelow(start.getBelow());
                start.getNext().getAbove().setBelow(start.getNext().getBelow());
                // consider removing the very bottom linked list (very bottom list's get below equals null)
                if (start.getBelow() != null) { // if null, then cannot call setAbove
                    start.getBelow().setAbove(start.getAbove());
                    start.getNext().getBelow().setAbove(start.getNext().getAbove());
                }
                start = start.getBelow();
                this.height--; // update the height of the skip list.
            } else empty = false; // if current list is not an empty list, then break the loop.
        }
    }

    // search the events at that time or floor events of the given time.
    private Node search(int time) {
        Node start = top.getHeader();
        while (start.hasBelow()) {
            start = start.getBelow();
            // scan forward if given time is greater or equal to the current node's next node's date.
            while (start.hasNext() && time >= Integer.parseInt(start.getNext().getContents().getTime())) {
                start = start.getNext();
            }
        }
        return start; // return the target node or floor node at the very bottom list.
    }

    // get method returns the exact event at the given date.
    public Node get(int date) {
        Node node = search(date);
        // return the node if the skip list contains the events happened at the given date and null if not
        if (Integer.parseInt(node.getContents().getTime()) == date) return node;
        else return null;
    }

    // floorEntry method returns the exact event or the closest event before the given date
    public Node floorEntry(int date) {
        Node node = search(date);
        // return null if there is no event before the given date.
        if (Integer.parseInt(node.getContents().getTime()) == 0) return null;
        return node;
    }

    // ceiling entry returns the exact event or the closest event after the date.
    public Node ceilingEntry(int date) {
        Node node = search(date);
        // return the node if there is an exact event at given date.
        if (Integer.parseInt(node.getContents().getTime()) == date) return node;
        // move to next node to get ceiling if there is no exact event.
        node = node.getNext();
        // return null if there is no event after the date.
        if (Integer.parseInt(node.getContents().getTime()) == 9999) return null;
        else return node; // else return the ceiling node.
    }

    // get all events between date1 and date2 inclusively
    public List<Node> subMap(int date1, int date2) {
        Node lowerBound = ceilingEntry(date1); // events after date1; ceiling entry
        Node upperBound = floorEntry(date2); // events before date2; floor entry
        List<Node> list = new ArrayList<>();
        if (lowerBound == null || upperBound == null) return list; // no node between two time stamps
        // add the node into the list from the lower bound to the upper bound inclusively
        while (Integer.parseInt(lowerBound.getContents().getTime()) <= Integer.parseInt(upperBound.getContents().getTime())) {
            list.add(lowerBound);
            lowerBound = lowerBound.getNext();
        }
        return list;
    }

    // add the event in the skip list. (dates are unique)
    public void add(String time, String event) {
        int randHeight = rand.get(); // get a random height to insert.
        int newHeight = randHeight - this.height;
        // add the empty list on the top of the list if inserting height is greater or equal to the current height.
        while (newHeight >= 0) {
            DoublyLinkedList temp = new DoublyLinkedList(); // new doubly linked list
            addList(temp); // add new empty list on the top of hte skip list
            newHeight--;
            this.height++; // increment the skip list's height
        }
        // add the events starting from the bottom level up until the randHeight.
        Node floor = search(Integer.parseInt(time)); // get the floor entry
        while (randHeight >= 0) { // add the event at the skip list starting at height 0 to randHeight.
            // temp node containing new event linking to the floor entry and floor entry.next.
            Node temp = new Node(new Contents(time ,event), null, null, floor, floor.getNext());
            floor.getNext().setPrev(temp); // update the floor.getNext()'s previous pointer
            floor.setNext(temp); // update the floor's next pointer.
            // move backward until the node that is also stored in the above linked list.
            while (!floor.hasAbove()){
                floor = floor.getPrev();
            }
            floor = floor.getAbove(); // go above linked list to store the time and event
            randHeight--;
        }
        // find the node right before the stored node in the highest skip list.
        floor = floor.getBelow();
        while (Integer.parseInt(floor.getNext().getContents().getTime()) != Integer.parseInt(time)) {
            floor = floor.getNext();
        }
        Node curPo = floor; // another pointer to keep track of the same node at one level below skip list.
        // link the same node in different level by above and below pointer.
        while (curPo.hasBelow()) { // curPo find and point the same node at one level lower linked list.
            curPo = curPo.getBelow();
            while (Integer.parseInt(curPo.getNext().getContents().getTime()) != Integer.parseInt(time)) {
                curPo = curPo.getNext();
            }
            // set the above and below pointer of the same nodes in different level
            curPo.getNext().setAbove(floor.getNext());
            floor.getNext().setBelow(curPo.getNext());
            // update the floor pointer to curPo to keep linking above and below pointers until the very bottom list.
            floor = curPo;
        }
    }

    // remove the node in the skip list.
    public Node remove(int date) {
        Node start = search(date); // find the node at the very bottom list.
        Node temp = start; // store the start node to return it.
        // return null if there is no exact event at that time.
        if (Integer.parseInt(start.getContents().getTime()) != date) return null;
        // change the deleting node's previous node and next node's pointer to skip itself.
        while (start != null) { // delete it for all level.
            start.getPrev().setNext(start.getNext());
            start.getNext().setPrev(start.getPrev());
            start = start.getAbove();
        }
        deleteList(); // update the list to maintain the skip list structure
        return temp;
    }
}

public class EventTrackingSystem {

    static SkipList skiplist = new SkipList(); // initialize skip list.

    public static void main(final String[] args) throws FileNotFoundException {
        // getting an input from the input file
        final File file = new File(args[0]);
        final Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            // store the input in an array.
            String[] input = sc.nextLine().split(" ");
            // call a method corresponds to the input command.
            switch(input[0]) {
                case "DisplayEvent":
                    displayEvent(input[1]);
                    break;
                case "AddEvent":
                    addEvent(input[1], input[2]);
                    break;
                case "DeleteEvent":
                    deleteEvent(input[1]);
                    break;
                case "DisplayEventsBetweenDates":
                    displayEvents(input[0], input[1], input[2]);
                    break;
                case "DisplayEventsFromStartDate":
                    displayEvents(input[0], input[1], "9998");
                    break;
                case "DisplayEventsToEndDate":
                    displayEvents(input[0], "1", input[1]);
                    break;
                case "DisplayAllEvents":
                    displayEvents(input[0], "1", "9998");
                    break;
                case "PrintSkipList":
                    printSkipList();
                    break;
                default:
                    System.out.println("wrong input");
            }
        }
    }

    // display the event occurred at the specific time.
    static void displayEvent (String date) {
        Node node = skiplist.get(Integer.parseInt(date)); // find the node in te skiplist.
        if (node == null) { // null if no event at that time
            System.out.println("DisplayEvent " + date + " none");
        } else { // else print out the event.
            System.out.println("DisplayEvent " + date + " " + node.getContents().getEvent());
        }
    }

    // add event occurred at the specific time. Assume time is unique for this assignment
    static void addEvent(String time, String event) {
        skiplist.add(time, event);
        System.out.println("AddEvent " + time + " " + event + " success");
    }

    // delete the event occurred at given time
    static void deleteEvent(String time) {
        Node node = skiplist.remove(Integer.parseInt(time));
        System.out.print("DeleteEvent " + time);
        if (node == null) System.out.println(" noDateError"); // no event in the skip list if null
        else System.out.println(" success");
    }

    // display all events between the start and end date.
    static void displayEvents(String command, String start, String end) {
        List<Node> list = skiplist.subMap(Integer.parseInt(start), Integer.parseInt(end));
        // print out the sentence corresponds to the call
        if (command.equals("DisplayEventsBetweenDates")) {
            System.out.print("DisplayEventsBetweenDates " + start + " " + end);
        } else if (command.equals("DisplayEventsFromStartDate")) {
            System.out.print("DisplayEventsFromStartDate " + start);
        } else if (command.equals("DisplayEventsToEndDate")) {
            System.out.print("DisplayEventsToEndDate " + end);
        } else if (command.equals("DisplayAllEvents")) {
            System.out.print("DisplayAllEvents");
        }
        // print none if no element in the list
        if (list.size() == 0) System.out.println(" none");
        else { //else print the date and event
            for (int i = 0; i < list.size(); i++) {
                Contents content = list.get(i).getContents();
                System.out.print(" " + content.getTime() + ":" + content.getEvent());
            }
            System.out.println();
        }
    }

    // print the entire skipList.
    static void printSkipList() {
        Node node = skiplist.getTop().getHeader(); // start  from the top list
        System.out.println("PrintSkipList");
        int height = skiplist.getHeight();
        System.out.println("(S" + height +")" + " empty"); // always the top list is an empty list.
        height--;
        node = node.getBelow();
        // print the skip list level by level
        while (node != null) {
            System.out.print("(S" + height +") ");
            Node temp = node;
            temp = temp.getNext(); // first node of each level is the header so skip it
            // print the time and event by iterating current list
            while (temp.getNext() != null) {
                System.out.print(temp.getContents().getTime()+":"+temp.getContents().getEvent()+" ");
                temp = temp.getNext();
            }
            System.out.println();
            // go to the below level
            node = node.getBelow();
            height--;
;        }
    }
}
