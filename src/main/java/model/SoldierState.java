package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import puzzle.State;

import java.util.*;

public class SoldierState implements State<Direction> {

    /**
     * The size of the soldier
     */
    public static final int SOLDIER = 0;

    /**
     * The size of the first black block.
     */
    public static final int BLACK_BLOCK1 = 1;

    /**
     * The size of the second black block.
     */
    public static final int BLACK_BLOCK2 = 2;

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 3;

    public IntegerProperty ACTIVE = new SimpleIntegerProperty(1);


    private final static ArrayList<Integer> RowCannon=new ArrayList<>(Arrays.asList(0,2,1,0,2,0,0,1,2,0,2,0,2,0,0));

    private final static ArrayList<Integer> ColumnCannon=new ArrayList<>(Arrays.asList(0,1,2,0,1,0,2,1,2,0,2,1,2,0,0));

    private final ReadOnlyObjectWrapper<Position>[] positions;
    private final ReadOnlyBooleanWrapper solved;

    /**
     * Creates a {@code SoldierState} object that corresponds to the initial state of the puzzle.
     */
    public SoldierState() {
        this( new Position(0, 0),
                new Position(13, 13),
                new Position(14, 6),
                new Position(14, 14));
    }

    /**
     * Creates a {@code SoldierState} object initializing the positions of the pieces with the positions specified.
     *
     * @param positions the initial positions of the pieces
     */
    public SoldierState(Position... positions) {
        this.positions = new ReadOnlyObjectWrapper[4];
        for (var i = 0; i < 4; i++) {
            this.positions[i] = new ReadOnlyObjectWrapper<>(positions[i]);
        }
        solved = new ReadOnlyBooleanWrapper();
        solved.bind(this.positions[0].isEqualTo(this.positions[3]));
    }

    /**
     * Returns the row index of the cannon for a given index.
     *
     * @param i the index of the cannon
     * @return the row index of the cannon
     */
    public Integer getCannonRowIndex(int i){
        return RowCannon.get(i);
    }

    /**
     * Returns the column index of the cannon for a given index.
     *
     * @param i the index of the cannon
     * @return the column index of the cannon
     */
    public Integer getCannonColumnIndex(int i){
        return ColumnCannon.get(i);
    }

    /**
     * Returns a copy of the position of the piece specified.
     *
     * @param n the number of a piece
     * @return the position of the piece
     */
    public Position getPosition(int n) {
        return positions[n].get();
    }

    /**
     * Returns the property of the position of the soldier.
     *
     * @return the property of the position of the soldier
     */
    public ReadOnlyObjectProperty<Position> positionProperty() {
        return positions[0].getReadOnlyProperty();
    }

    /**
     * Returns whether the puzzle is solved.
     *
     * @return true if the puzzle is solved, false otherwise
     */
    public boolean isSolved() {
        return solved.get();
    }
    public ReadOnlyBooleanProperty solvedProperty() {
        return solved.getReadOnlyProperty();
    }

    /**
     * Checks if the black block can be moved to the specified position.
     *
     * @param position the position to check
     * @return true if the black block can be moved, false otherwise
     */
    private boolean canMoveBlackBlock(Position position){
        if (position.equals(getPosition(BLACK_BLOCK1))||
                position.equals(getPosition(BLACK_BLOCK2))){
            return false;
        }
        return true;
    }

    /**
     * Returns whether the move in the specified direction is legal.
     *
     * @param direction a direction to which the soldier is intended to be moved
     * @return whether the move in the specified direction is legal
     */
    @Override
    public boolean isLegalMove(Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp();
            case RIGHT -> canMoveRight();
            case DOWN -> canMoveDown();
            case LEFT -> canMoveLeft();
        };
    }

    private boolean canMoveUp() {

        var up = getPosition(SOLDIER).moveUp();
        if(getPosition(SOLDIER).row() > 0 &&
                canMoveBlackBlock(up) &&
                ((getCannonColumnIndex(up.row())==ACTIVE.get() && getCannonRowIndex(up.col())==ACTIVE.get())
                        || (getCannonColumnIndex(up.row())==0 && getCannonRowIndex(up.col())==0)
                        || (getCannonColumnIndex(up.row())==ACTIVE.get() && getCannonRowIndex(up.col())!=ACTIVE.get())
                        || (getCannonColumnIndex(up.row())!=ACTIVE.get() && getCannonRowIndex(up.col())==ACTIVE.get()))){

            return isEmpty(up);
        }
        return false;
    }
    private boolean canMoveRight() {
        var right = getPosition(SOLDIER).moveRight();

        if(getPosition(SOLDIER).col() < getPosition(BOARD_SIZE).col() &&
                canMoveBlackBlock(right) &&
                ((getCannonRowIndex(right.col())==ACTIVE.get() && getCannonColumnIndex(right.row())==ACTIVE.get())
                        || (getCannonRowIndex(right.col())==0 && getCannonColumnIndex(right.row())==0)
                        || (getCannonRowIndex(right.col())==ACTIVE.get() && getCannonColumnIndex(right.row())!=ACTIVE.get())
                        || (getCannonRowIndex(right.col())!=ACTIVE.get() && getCannonColumnIndex(right.row())==ACTIVE.get()))){

            return isEmpty(right);
        }
        return false;
    }
    private boolean canMoveDown() {
        var down = getPosition(SOLDIER).moveDown();

        if(getPosition(SOLDIER).row() < getPosition(BOARD_SIZE).row() &&
                canMoveBlackBlock(down) &&
                ((getCannonColumnIndex(down.row())==ACTIVE.get() && getCannonRowIndex(down.col())==ACTIVE.get())
                        || (getCannonColumnIndex(down.row())==0 && getCannonRowIndex(down.col())==0)
                        || (getCannonColumnIndex(down.row())==ACTIVE.get() && getCannonRowIndex(down.col())!=ACTIVE.get())
                        || (getCannonColumnIndex(down.row())!=ACTIVE.get() && getCannonRowIndex(down.col())==ACTIVE.get()))){

            return isEmpty(down);
        }
        return false;
    }

    private boolean canMoveLeft() {
        var left = getPosition(SOLDIER).moveLeft();

        if(getPosition(SOLDIER).col() >0 &&
                canMoveBlackBlock(left) &&
                ((getCannonRowIndex(left.col())==ACTIVE.get() && getCannonColumnIndex(left.row())==ACTIVE.get())
                        || (getCannonRowIndex(left.col())==0 && getCannonColumnIndex(left.row())==0)
                        || (getCannonRowIndex(left.col())==ACTIVE.get() && getCannonColumnIndex(left.row())!=ACTIVE.get())
                        || (getCannonRowIndex(left.col())!=ACTIVE.get() && getCannonColumnIndex(left.row())==ACTIVE.get()))){

            return isEmpty(left);
        }
        return false;
    }

    public boolean isEmpty(Position position) {
        for (var p : positions) {
            if (p.equals(position) && p==positions[3] ) {
                return true;
            }
            else if (p.equals(position)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Moves the soldier to the direction specified.
     *
     * @param direction the direction to which the soldier is moved
     */
    @Override
    public void makeMove(Direction direction) {
        switch (direction) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        }
        updateActiveCannon();
    }
    private void updateActiveCannon() {
        ACTIVE.set((ACTIVE.get() == 1 ? 2 : 1));
    }

    private void moveUp() {
        moveSoldier(Direction.UP);


    }
    private void moveRight() {
        moveSoldier(Direction.RIGHT);

    }
    private void moveDown() {
        moveSoldier(Direction.DOWN);

    }
    private void moveLeft() {
        moveSoldier(Direction.LEFT);

    }

    /**
     * Moves the soldier to the direction specified.
     *
     * @param direction the direction to which the soldier is moved
     */
    private void moveSoldier(Direction direction) {
        var newPosition = getPosition(0).move(direction);
        positions[0].set(newPosition);
    }

    /**
     * Returns the set of legal moves for the soldier.
     *
     * @return the set of legal moves
     */
    @Override
    public Set<Direction> getLegalMoves() {
        var legalMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (isLegalMove(direction)) {
                legalMoves.add(direction);
            }
        }
        return legalMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof SoldierState other)
                && getPosition(SOLDIER).equals(other.getPosition(SOLDIER))
                && getPosition(BLACK_BLOCK1).equals(other.getPosition(BLACK_BLOCK1))
                && getPosition(BLACK_BLOCK2).equals(other.getPosition(BLACK_BLOCK2))
                && getPosition(BOARD_SIZE).equals(other.getPosition(BOARD_SIZE));    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(SOLDIER), getPosition(BLACK_BLOCK1),
                getPosition(BLACK_BLOCK2), getPosition(BOARD_SIZE));
    }

    @Override
    public SoldierState clone() {
        SoldierState copy = new SoldierState(getPosition(SOLDIER), getPosition(BLACK_BLOCK1),
                getPosition(BLACK_BLOCK2), getPosition(BOARD_SIZE));

        copy.ACTIVE.set(this.ACTIVE.get());
        return copy;

    }

    @Override
    public String toString() {
        var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
            sj.add(position.toString());
        }
        return sj.toString();
    }
}
