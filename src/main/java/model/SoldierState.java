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

    public static final int SOLDIER = 0;

    /**
     * Az egyik fekete tilos mező indexe
     */
    public static final int BLACK_BLOCK1 = 1;

    /**
     * A másik fekete tilos mező indexe
     */
    public static final int BLACK_BLOCK2 = 2;

    /**
     * Atábla méretének indexe
     */
    public static final int BOARD_SIZE = 3;
    /**
     * Az aktív ágyú indexe
     */
    public IntegerProperty ACTIVE = new SimpleIntegerProperty(1);
    /**
     * A lépések számának változója
     */
    public IntegerProperty MOVES = new SimpleIntegerProperty();


    private final static ArrayList<Integer> RowCannon=new ArrayList<>(Arrays.asList(0,2,1,0,2,0,0,1,2,0,2,0,2,0,0));

    private final static ArrayList<Integer> ColumnCannon=new ArrayList<>(Arrays.asList(0,1,2,0,1,0,2,1,2,0,2,1,2,0,0));

    private final ReadOnlyObjectWrapper<Position>[] positions;
    private final ReadOnlyBooleanWrapper solved;

    public SoldierState() {
        this( new Position(0, 0),
                new Position(13, 13),
                new Position(14, 6),
                new Position(14, 14));
    }
    public SoldierState(Position... positions) {

        //this.positions = positions.clone();
        this.positions = new ReadOnlyObjectWrapper[4];
        for (var i = 0; i < 4; i++) {
            this.positions[i] = new ReadOnlyObjectWrapper<>(positions[i]);
        }
        solved = new ReadOnlyBooleanWrapper();
        solved.bind(this.positions[0].isEqualTo(this.positions[3]));
    }


    /*public void numberOfMovesForMove(IntegerProperty number){
        this.MOVES=number;
    }*/

    public Integer getCannonRowIndex(int i){
        return RowCannon.get(i);
    }

    public Integer getCannonColumnIndex(int i){
        return ColumnCannon.get(i);
    }

    public Position getPosition(int n) {
        return positions[n].get();
    }

    public ReadOnlyObjectProperty<Position> positionProperty() {
        return positions[0].getReadOnlyProperty();
    }

    public boolean isSolved() {
        return solved.get();
    }
    public ReadOnlyBooleanProperty solvedProperty() {
        return solved.getReadOnlyProperty();
    }

    private boolean haveEqualPositions(int i, int j) {
        return positions[i].equals(positions[j]);
    }

    private boolean canMoveBlackBlock(Position position){
        if (position.equals(getPosition(BLACK_BLOCK1))||
                position.equals(getPosition(BLACK_BLOCK2))){
            return false;
        }
        return true;
    }

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
    private void moveSoldier(Direction direction) {
        var newPosition = getPosition(0).move(direction);
        positions[0].set(newPosition);
    }

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
