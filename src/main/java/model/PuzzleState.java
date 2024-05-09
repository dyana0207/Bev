package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import puzzle.State;

import java.util.*;

public class PuzzleState implements State<Direction> {

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

    private static ArrayList<Integer> RowCannon=new ArrayList<>(Arrays.asList(0,2,1,0,2,0,0,1,2,0,2,0,2,0,0));

    private static ArrayList<Integer> ColumnCannon=new ArrayList<>(Arrays.asList(0,1,2,0,1,0,2,1,2,0,2,1,2,0,0));

    private Position[] positions;

    public PuzzleState() {
        this( new Position(0, 0),
                new Position(13, 13),
                new Position(14, 6),
                new Position(14, 14));
    }

    public PuzzleState(Position... positions) {
        this.positions = positions.clone();
    }


    public void numberOfMovesForMove(IntegerProperty number){
        this.MOVES=number;
    }

    public Integer getCannonRowIndex(int i){
        return RowCannon.get(i);
    }

    public Integer getCannonColumnIndex(int i){
        return ColumnCannon.get(i);
    }

    public Position getPosition(int n) {
        return positions[n];
    }


    @Override
    public boolean isSolved() {
        return haveEqualPositions(SOLDIER, BOARD_SIZE);
    }

    private boolean haveEqualPositions(int i, int j) {
        return positions[i].equals(positions[j]);
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

        var up = positions[SOLDIER].moveUp();

        if(positions[SOLDIER].row() > 0 &&
                ((getCannonColumnIndex(up.row())==ACTIVE.get() && getCannonRowIndex(up.col())==ACTIVE.get())
                        || (getCannonColumnIndex(up.row())==0 && getCannonRowIndex(up.col())==0)
                        || (getCannonColumnIndex(up.row())==ACTIVE.get() && getCannonRowIndex(up.col())!=ACTIVE.get())
                        || (getCannonColumnIndex(up.row())!=ACTIVE.get() && getCannonRowIndex(up.col())==ACTIVE.get()))){

            return isEmpty(up);
        }
        return false;
    }
    private boolean canMoveRight() {
        var right = positions[SOLDIER].moveRight();

        if(positions[SOLDIER].col() < positions[BOARD_SIZE].col() &&
                ((getCannonRowIndex(right.col())==ACTIVE.get() && getCannonColumnIndex(right.row())==ACTIVE.get())
                        || (getCannonRowIndex(right.col())==0 && getCannonColumnIndex(right.row())==0)
                        || (getCannonRowIndex(right.col())==ACTIVE.get() && getCannonColumnIndex(right.row())!=ACTIVE.get())
                        || (getCannonRowIndex(right.col())!=ACTIVE.get() && getCannonColumnIndex(right.row())==ACTIVE.get()))){

            return isEmpty(right);
        }
        return false;
    }
    private boolean canMoveDown() {
        var down = positions[SOLDIER].moveDown();

        if(positions[SOLDIER].row() < positions[BOARD_SIZE].row() &&
                ((getCannonColumnIndex(down.row())==ACTIVE.get() && getCannonRowIndex(down.col())==ACTIVE.get())
                        || (getCannonColumnIndex(down.row())==0 && getCannonRowIndex(down.col())==0)
                        || (getCannonColumnIndex(down.row())==ACTIVE.get() && getCannonRowIndex(down.col())!=ACTIVE.get())
                        || (getCannonColumnIndex(down.row())!=ACTIVE.get() && getCannonRowIndex(down.col())==ACTIVE.get()))){

            return isEmpty(down);
        }
        return false;
    }

    private boolean canMoveLeft() {
        var left = positions[SOLDIER].moveLeft();

        if(positions[SOLDIER].col() >0 &&
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
       // System.out.println(ACTIVE);
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
        var newPosition =  positions[SOLDIER].move(direction);
        positions[SOLDIER] = newPosition;
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

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE &&
                position.col() >= 0 && position.col() < BOARD_SIZE;
    }


    @Override
    public PuzzleState clone() {
        PuzzleState copy = null;
        try {
            copy = (PuzzleState) super.clone();
            // Az ACTIVE és MOVES tulajdonságok másolása
            copy.ACTIVE = new SimpleIntegerProperty(this.ACTIVE.get());
            copy.MOVES = new SimpleIntegerProperty(this.MOVES.get());
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        copy.positions = positions.clone();
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
