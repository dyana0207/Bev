package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class SoldierStateTest {

    private SoldierState soldierState;
    private SoldierState goalState;
    private SoldierState nonGoalState;
    private SoldierState deadEndState;

    @BeforeEach
    void setUp() {
        soldierState = new SoldierState(
                new Position(0, 0),
                new Position(13, 13),
                new Position(14, 6),
                new Position(14, 14)
        );

        goalState = new SoldierState(
                new Position(14, 14),
                new Position(13, 13),
                new Position(14, 6),
                new Position(14, 14)
        );

        nonGoalState = new SoldierState(
                new Position(1, 1),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)
        );

        deadEndState = new SoldierState(
                new Position(0, 0),
                new Position(1, 0),
                new Position(0, 1),
                new Position(0, 0)
        );
    }

    @Test
    void testInitialState() {
        assertEquals(new Position(0, 0), soldierState.getPosition(SoldierState.SOLDIER));
        assertEquals(new Position(13, 13), soldierState.getPosition(SoldierState.BLACK_BLOCK1));
        assertEquals(new Position(14, 6), soldierState.getPosition(SoldierState.BLACK_BLOCK2));
        assertEquals(new Position(14, 14), soldierState.getPosition(SoldierState.BOARD_SIZE));
    }

    @Test
    void testIsSolved() {
        assertFalse(soldierState.isSolved());
        assertTrue(goalState.isSolved());
        assertFalse(nonGoalState.isSolved());
        assertTrue(deadEndState.isSolved());
    }

    @Test
    void testIsLegalMove() {
        // soldierState tests
        assertFalse(soldierState.isLegalMove(Direction.UP));
        assertFalse(soldierState.isLegalMove(Direction.RIGHT));
        assertTrue(soldierState.isLegalMove(Direction.DOWN));
        assertFalse(soldierState.isLegalMove(Direction.LEFT));

        // deadEndState tests
        assertFalse(deadEndState.isLegalMove(Direction.UP));
        assertFalse(deadEndState.isLegalMove(Direction.RIGHT));
        assertFalse(deadEndState.isLegalMove(Direction.DOWN));
        assertFalse(deadEndState.isLegalMove(Direction.LEFT));
    }

    @Test
    void testMakeMove() {
        var stateBeforeMove = soldierState.clone();
        soldierState.makeMove(Direction.RIGHT);
        assertEquals(stateBeforeMove.getPosition(SoldierState.SOLDIER).moveRight(), soldierState.getPosition(SoldierState.SOLDIER));
        assertEquals(stateBeforeMove.getPosition(SoldierState.BLACK_BLOCK1), soldierState.getPosition(SoldierState.BLACK_BLOCK1));
        assertEquals(stateBeforeMove.getPosition(SoldierState.BLACK_BLOCK2), soldierState.getPosition(SoldierState.BLACK_BLOCK2));
        assertEquals(stateBeforeMove.getPosition(SoldierState.BOARD_SIZE), soldierState.getPosition(SoldierState.BOARD_SIZE));
    }


    @Test
    void testEquals() {
        assertTrue(soldierState.equals(soldierState));
        var clone = soldierState.clone();
        clone.makeMove(Direction.RIGHT);
        assertFalse(clone.equals(soldierState));
        assertFalse(soldierState.equals(null));
        assertFalse(soldierState.equals("Hello, World!"));
        assertFalse(soldierState.equals(goalState));
    }

    @Test
    void testHashCode() {
        assertEquals(soldierState.hashCode(), soldierState.hashCode());
        assertEquals(soldierState.hashCode(), soldierState.clone().hashCode());
    }

    @Test
    void testClone() {
        var clone = soldierState.clone();
        assertTrue(clone.equals(soldierState));
        assertNotSame(clone, soldierState);
    }

}
