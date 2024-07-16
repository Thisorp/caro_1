package com.example.caro_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {
    final static int maxN = 15; // cập nhật bảng size
    private Context context;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];

    private Drawable[] drawCell = new Drawable[4]; // 0 is empty, 1 is player1, 2 is player2, 3 is background

    private Button btnPlay;
    private TextView tvTurn;

    private int[][] valueCell = new int[maxN][maxN]; // 0 is empty, 1 is player1, 2 is player2
    private int winner_play; // 0 is no one, 1 is player1, 2 is player2
    private boolean firstMove;
    private int xMove, yMove; // x and y axis of cell
    private int turnPlay; // whose turn?
    private int countX, countO; // Đếm số lượng X và O

    private boolean isClicked;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setListen();
        loadResources();
        designBoardGame();
    }

    private void setListen() {
        btnPlay = findViewById(R.id.btnPlay);
        tvTurn = findViewById(R.id.tvTurn);

        btnPlay.setText("New Game");
        tvTurn.setText("Bấm nút New Game để bắt đầu");

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_game();
                start_game();
            }
        });
    }

    private void start_game() {
        Random r = new Random();
        turnPlay = r.nextInt(2) + 1; // r.nextInt(2) return value in [0,1]

        if (turnPlay == 1) {
            Toast.makeText(context, "Player 1 đi trước!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Player 2 đi trước!", Toast.LENGTH_SHORT).show();
        }
        playerTurn();
    }

    private void make_a_move() {
        Log.d("turban", "make a move with " + xMove + ";" + yMove + ";" + turnPlay);
        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;

        // Cập nhật đếm số X và O
        if (turnPlay == 1) {
            countX++;
        } else {
            countO++;
        }

        if (noEmptyCell()) {
            Toast.makeText(context, "Draw!!!", Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckWinner()) {
            if (winner_play == 1) {
                Toast.makeText(context, "Winner is Player 1", Toast.LENGTH_SHORT).show();
                tvTurn.setText("Player 1 chiến thắng !!!\nSố dấu X: " + countX + "\nSố dấu O: " + countO);
            } else {
                Toast.makeText(context, "Winner is Player 2", Toast.LENGTH_SHORT).show();
                tvTurn.setText("Player 2 chiến thắng !!!\nSố dấu X: " + countX + "\nSố dấu O: " + countO);
            }
            return;
        }

        turnPlay = (turnPlay == 1) ? 2 : 1;
        playerTurn();
    }



    private boolean CheckWinner() {
        if (winner_play != 0) return true;
        VectorEnd(xMove, 0, 0, 1, xMove, yMove);
        VectorEnd(0, yMove, 1, 0, xMove, yMove);
        if (xMove + yMove >= maxN - 1) {
            VectorEnd(maxN - 1, xMove + yMove - maxN + 1, -1, 1, xMove, yMove);
        } else {
            VectorEnd(xMove + yMove, 0, -1, 1, xMove, yMove);
        }
        if (xMove <= yMove) {
            VectorEnd(xMove - yMove + maxN - 1, maxN - 1, -1, -1, xMove, yMove);
        } else {
            VectorEnd(maxN - 1, maxN - 1 - (xMove - yMove), -1, -1, xMove, yMove);
        }
        if (winner_play != 0) {
            tvTurn.setText(String.format("Player %d chiến thắng!!!\nSố dấu X: %d\nSố dấu O: %d", winner_play, countX, countO));
            return true;
        }
        return false;
    }



    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {
        if (winner_play != 0) return;
        final int range = 4;
        int i, j;
        int xbelow = rx - range * vx;
        int ybelow = ry - range * vy;
        int xabove = rx + range * vx;
        int yabove = ry + range * vy;
        String st = "";
        i = xx;
        j = yy;
        while (!inside(i, xbelow, xabove) || !inside(j, ybelow, yabove)) {
            i += vx;
            j += vy;
        }
        while (true) {
            st = st + String.valueOf(valueCell[i][j]);
            if (st.length() == 5) {
                EvalEnd(st);
                st = st.substring(1, 5);
            }
            i += vx;
            j += vy;
            if (!inBoard(i, j) || !inside(i, xbelow, xabove) || !inside(j, ybelow, yabove) || winner_play != 0) {
                break;
            }
        }
    }

    private boolean inBoard(int i, int j) {
        return i >= 0 && i < maxN && j >= 0 && j < maxN;
    }

    private void EvalEnd(String st) {
        switch (st) {
            case "11111":
                winner_play = 1;
                break;
            case "22222":
                winner_play = 2;
                break;
            default:
                break;
        }
    }

    private boolean inside(int i, int xbelow, int xabove) {
        return (i - xbelow) * (i - xabove) <= 0;
    }

    private boolean noEmptyCell() {
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++)
                if (valueCell[i][j] == 0) return false;
        }
        return true;
    }

    private void playerTurn() {
        Log.d("tuanh", "player turn");
        if (turnPlay == 1) {
            tvTurn.setText("Player 1");
        } else {
            tvTurn.setText("Player 2");
        }
        firstMove = false;
        isClicked = false;
    }

    private void init_game() {
        firstMove = true;
        winner_play = 0;
        countX = 0; // Đặt lại đếm X
        countO = 0; // Đặt lại đếm O
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j] = 0;
            }
        }
    }


    private void loadResources() {
        drawCell[3] = context.getResources().getDrawable(R.drawable.o);
        drawCell[0] = null;
        drawCell[1] = context.getResources().getDrawable(R.drawable.x_cell);
        drawCell[2] = context.getResources().getDrawable(R.drawable.o_cell);
    }

    @SuppressLint("NewApi")
    private void designBoardGame() {
        int sizeofCell = Math.round(ScreenWidth() / maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell * maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);

        LinearLayout linBoardGame = findViewById(R.id.linBoardGame);

        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j] = new ImageView(context);
                ivCell[i][j].setBackground(drawCell[3]);
                final int x = i;
                final int y = j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (valueCell[x][y] == 0) {
                            if (!isClicked) {
                                Log.d("tuanh", "click to cell ");
                                isClicked = true;
                                xMove = x;
                                yMove = y;
                                make_a_move();
                            }
                        }
                    }
                });
                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
}
