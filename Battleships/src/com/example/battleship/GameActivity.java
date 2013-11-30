package com.example.battleship;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

@SuppressLint("NewApi")
public class GameActivity extends Activity {
  
/** Called when the activity is first created. */
	int attackType = 0;
	int houseCount = 0;
	int[] houses = new int[10];
	int attackCount = 0;
	int[] attackLocations = { -1, -1, -1, -1, -1, -1, -1, -1, -1};
	boolean[] pastAttacks = new boolean[64];
	String mapAsString = "";
	final int GRID_WIDTH = 8;
	final int GRID_HEIGHT = 8;
	final int MAX_HOUSES = 25;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	//Remove title bar
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_game);
	TextView housesLeftTextView = (TextView) findViewById(R.id.housesLeft);
	housesLeftTextView.setText(25 - houseCount + " houses left to place.");
	Arrays.fill(pastAttacks, false);
    Communications.setActivity(this);
    // Send ready
  }
  
  public void onGridToggleButtonClick(View view)
  {
	  TextView housesLeftTextView = (TextView) findViewById(R.id.housesLeft);
	  housesLeftTextView.setText(25 - houseCount + " houses left to place.");
	  if(((ToggleButton) view).isChecked())
	  {
		  if(checkNumberOfHouses() <= MAX_HOUSES)
		  {
			  ((ToggleButton) view).setBackgroundResource(R.drawable.house);
			  housesLeftTextView.setText(25 - houseCount + " houses left to place.");
		  }
		  else
		  {
			  ((ToggleButton) view).setChecked(false);
		  }
	  }
	  else
	  {
		  ((ToggleButton) view).setBackgroundResource(R.drawable.shape);
	  }
  }
  
  private int checkNumberOfHouses()
  {
	  int count = 0;
	  GridLayout grid =(GridLayout) findViewById(R.id.mainGrid);
	  for(int i = 0; i < grid.getChildCount(); i++)
	  {
		  if(((ToggleButton) grid.getChildAt(i)).isChecked())
			  count++;
	  }
	  houseCount = count;
	  if(houseCount > MAX_HOUSES)
		  houseCount = MAX_HOUSES;
	  return count;
  }
  
  public void populateOpponentGrid(String receivedMessage)
  {
	  String houseLocations = receivedMessage.substring(1);
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i < grid.getChildCount(); i++)
	  {
		  if(houseLocations.indexOf(0) == 'B')
			  ((ToggleButton) grid.getChildAt(i)).setChecked(true);
		  if(houseLocations.length() == 1)
			  break;
		  houseLocations = houseLocations.substring(1);
	  }
  }
  
  public void updateOpponentGrid(String receivedMessage)
  {
	  String opponentMap = receivedMessage;
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i< grid.getChildCount(); i++)
	  {
		  if(opponentMap.indexOf(1) == 'B')
		  {
			  ((ToggleButton) grid.getChildAt(i)).setBackgroundResource(R.drawable.house);
		  }
	  }
  }
  
  public void onOpponentHouseToggleButtonClick(View view)
  {
	  ToggleButton opponentHouse = (ToggleButton) view;
	  removePreviousBasicAttackSelection();
	  if(opponentHouse.isChecked())
	  {
		  addAttackLocations(opponentHouse);
			  if(opponentHouse.getBackground().equals(R.drawable.house))
				  opponentHouse.setBackgroundResource(R.drawable.opponent_house);
			  else
				  opponentHouse.setBackgroundResource(R.drawable.attack_location); // change to attack location. maybe an X
		  //}
	  }
	  else
	  {
		  //--;
		  if(opponentHouse.getBackground().equals(R.drawable.opponent_house))
			  opponentHouse.setBackgroundResource(R.drawable.house);
		  else
			  opponentHouse.setBackgroundResource(R.drawable.shape);
	  }
  }
  
  void addAttackLocations(ToggleButton location)
  {
	  attackLocations[attackCount] = getIndexInParent(location);
	  attackCount++;
  }
  
  int getIndexInParent(ToggleButton view)
  {
	  int index = -1;
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i < grid.getChildCount(); i++)
	  {
		  if(grid.getChildAt(i).getId() == view.getId())
		  {
			  index = i;
			  break;
		  }
	  }
	  return index;
  }
  
  void removePreviousBasicAttackSelection()
  {
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i < attackCount; i++)
	  {
		  if(attackLocations[i] >= 0)
		  {
			  ((ToggleButton) grid.getChildAt(attackLocations[i])).setChecked(false);
			  ((ToggleButton) grid.getChildAt(attackLocations[i])).setBackgroundResource(R.drawable.shape);
		  }
	  }
	  Arrays.fill(attackLocations, -1);
	  attackCount = 0;
  }
  
  void updateAttackPlacement(GridLayout grid)
  {
	  for(int i = 0; i < attackCount; i++)
	  {
		  ((ToggleButton) grid.getChildAt(i)).setChecked(false);
		  ((ToggleButton) grid.getChildAt(i)).setBackgroundResource(R.drawable.shape);
	  }
	  for(int i = 0; i < attackCount; i++)
	  {
		  if(attackLocations[i] >= 0)
		  {
			  ((ToggleButton) grid.getChildAt(attackLocations[i])).setChecked(true);
			  ((ToggleButton) grid.getChildAt(attackLocations[i])).setBackgroundResource(R.drawable.attack_location);
		  }
	  }
  }
  
  public void onResetAttackButtonClick(View view)
  {
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i< grid.getChildCount(); i++)
	  {
		  if(((ToggleButton) grid.getChildAt(i)).isChecked())
		  {
			  ((ToggleButton) grid.getChildAt(i)).setChecked(false);
			  ((ToggleButton) grid.getChildAt(i)).setBackgroundResource(R.drawable.shape);
		  }
	  }
  }
  
  public void onHit(int x, int y)
  {
	  int shiftedX = x - 'A';
	  int shiftedY = y - 'A';
	  
	  int hitIndex = shiftedX + (8 * shiftedY);
			  
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  
	  ((ToggleButton) grid.getChildAt(hitIndex)).setChecked(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setClickable(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setBackgroundResource(R.drawable.attack_hit);
  }
  
  public void onMiss(int x, int y)
  {
	  int shiftedX = x - 'A';
	  int shiftedY = y - 'A';
	  
	  int hitIndex = shiftedX + (8 * shiftedY);
			  
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  
	  ((ToggleButton) grid.getChildAt(hitIndex)).setChecked(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setClickable(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setBackgroundResource(R.drawable.attack_miss);
  }
  
  public void onSelfMiss(int x, int y)
  {
	  int shiftedX = x - 'A';
	  int shiftedY = y - 'A';
	  
	  int hitIndex = shiftedX + (8 * shiftedY);
			  
	  GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
	  
	  ((ToggleButton) grid.getChildAt(hitIndex)).setChecked(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setClickable(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setBackgroundResource(R.drawable.attack_miss);
  }
  
  public void onSelfHit(int x, int y)
  {
	  int shiftedX = x - 'A';
	  int shiftedY = y - 'A';
	  
	  int hitIndex = shiftedX + (8 * shiftedY);
			  
	  GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
	  
	  ((ToggleButton) grid.getChildAt(hitIndex)).setChecked(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setClickable(false);
	  ((ToggleButton) grid.getChildAt(hitIndex)).setBackgroundResource(R.drawable.attack_hit);
  }
  
  public void onConfirmAttackButtonClick(View view)
  {
	  int attackIndex = placeBasicAttack();
	  pastAttacks[attackIndex] = true;
	  String attackMessage = makeAttackMessage(attackIndex);
	  //((TextView) findViewById(R.id.attackMessageText)).setText(attackMessage);
	  sendMessage(attackMessage);
  }
  
  public int placeBasicAttack()
  {
	  int attackIndex = -1;
	  GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
	  for(int i = 0; i< grid.getChildCount(); i++)
	  {
		  if(((ToggleButton) grid.getChildAt(i)).isChecked())
		  {
			  attackIndex = i;
			  ((ToggleButton) grid.getChildAt(i)).setClickable(false);
			  break;
		  }
	  }
	  return attackIndex;
  }
  
  public String makeAttackMessage(int attackLocation)
  {
	  String attackMessage = "EA";
	  String attackLocationString = "";
	  // translate to b/w AA and EE
	  int column = attackLocation / GRID_WIDTH;
	  int row = attackLocation % GRID_HEIGHT;
	  
	  switch (row)
	  {
	  case 0:
		  attackLocationString += 'A';
		  break;
	  case 1:
		  attackLocationString += 'B';
		  break;
	  case 2:
		  attackLocationString += 'C';
		  break;
	  case 3:
		  attackLocationString += 'D';
		  break;
	  case 4:
		  attackLocationString += 'E';
		  break;
	  case 5:
		  attackLocationString += 'F';
		  break;
	  case 6:
		  attackLocationString += 'G';
		  break;
	  case 7:
		  attackLocationString += 'H';
		  break;
	  default:
		  break;			 
	  }
	  
	  switch (column)
	  {
	  case 0:
		  attackLocationString += 'A';
		  break;
	  case 1:
		  attackLocationString += 'B';
		  break;
	  case 2:
		  attackLocationString += 'C';
		  break;
	  case 3:
		  attackLocationString += 'D';
		  break;
	  case 4:
		  attackLocationString += 'E';
		  break;
	  case 5:
		  attackLocationString += 'F';
		  break;
	  case 6:
		  attackLocationString += 'G';
		  break;
	  case 7:
		  attackLocationString += 'H';
		  break;
	  default:
		  break;			 
	  }
	  attackMessage += attackLocationString + attackLocationString;
	  return attackMessage;
  }
  
  /*private int checkAdjacent(View view)
  {
	  GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
	  int numViewInGrid = grid.getChildCount();
	  int viewNumber = -1;
	  int numAdjacent = 0;
	  for(int i = 0; i < numViewInGrid; i++)
	  {
		  if(grid.getChildAt(i).getId() == view.getId())
		  {
			  viewNumber = i;
			  break;
		  }
	  }
	  if(viewNumber >= 0)
	  {
		  for(int i = viewNumber+1; (i%5 > 0) && (!grid.getChildAt(i).isClickable()); i++)
		  {
			  numAdjacent++;
		  }
		  if(viewNumber%5 > 0)
		  {
			  for(int i = viewNumber - 1;(i%5 >= 0) && (!grid.getChildAt(i).isClickable()); i++)
			  {
				  numAdjacent++;
			  }
		  }
	  }
	  
	  return numAdjacent;
  }*/
  
  public void onConfirmButtonClick(View view)
  {
	  findViewById(R.id.housesLeft).setVisibility(view.INVISIBLE);
	  mapAsString = "";
	  if(checkNumberOfHouses() == MAX_HOUSES)
	  {
		  mapAsString += 'C';
		  GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
		  for(int i = 0; i < grid.getChildCount(); i++)
		  {
			  ToggleButton house = (ToggleButton) grid.getChildAt(i);
			  if(house.isChecked())
			  {
				  mapAsString += 'B';
				  house.setClickable(false);
			  }
			  else
			  {
				  mapAsString += 'A';
				  house.setClickable(false);
			  }
		  }
		  findViewById(R.id.resetButton).setClickable(false);
		  findViewById(R.id.resetButton).setVisibility(View.GONE);
		  findViewById(R.id.confirmButton).setClickable(false);
		  findViewById(R.id.confirmButton).setVisibility(View.GONE);
		  sendMessage(mapAsString);
		  setWaitingForPlayersToBeReady();
	  }
	  else
	  {
		  String toPrint = "Need to place " + MAX_HOUSES + " houses.";
		  ((TextView) findViewById(R.id.messageText)).setText(toPrint);
	  }
  }
  
  private void setWaitingForPlayersToBeReady()
  {
	  /*Intent intent = new Intent(this, OverlayService.class);
	  intent.putExtra("ALERT_TEXT", "Waiting for all players to be ready.");
	  startService(intent);*/
	  findViewById(R.id.overlay).setVisibility(View.VISIBLE);
	  disableBoard();
  }
  
  public void setAllPlayersNowReady()
  {
	  /*Intent intent = new Intent(this, OverlayService.class);
	  stopService(intent);*/
	  findViewById(R.id.overlay).setVisibility(View.GONE);
	  enableBoard();
  }
  
  public void setOpponentsTurn()
  {
	  /*Intent intent = new Intent(this, OverlayService.class);
	  intent.putExtra("ALERT_TEXT", "Opponnent's turn!");
	  startService(intent);*/
	  findViewById(R.id.overlay).setVisibility(View.VISIBLE);
	  disableBoard();
  }
  
  public void setYourTurn()
  {
	  /*Intent intent = new Intent(this, OverlayService.class);
	  stopService(intent);*/
	  findViewById(R.id.overlay).setVisibility(View.GONE);
	  enableBoard();
  }
  
  public void onResetButtonClick(View view)
  {
	  GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
	  for(int i = 0; i < grid.getChildCount(); i++)
	  {
		  ToggleButton house = (ToggleButton) grid.getChildAt(i);
		  if(house.isChecked())
		  {
			  house.setBackgroundResource(R.drawable.shape);
			  house.setChecked(false);
			  houseCount--;
		  }
	  }
  }
  
	//  Called when the user wants to send a message
	public void sendMessage(String message)
	{
		Communications.getCommunications().sendMessage(message);
	}
	
	public void updatePlayerHouse(String receivedMessage)
	{
		GridLayout grid = (GridLayout) findViewById(R.id.mainGrid);
		int column;
		int row;
		int index;
		String houseToUpdate = receivedMessage; //change
		switch (houseToUpdate.charAt(0))
		{
		case 'A':
			row = 0;
			break;
		case 'B':
			row = 1;
			break;
		case 'C':
			row = 2;
			break;
		case 'D':
			row = 3;
			break;
		case 'E':
			row = 4;
			break;
		case 'F':
			row = 5;
			break;
		case 'G':
			row = 6;
			break;
		case 'H':
			row = 7;
			break;
		default:
			row = -1;
		}
		
		switch (houseToUpdate.charAt(1))
		{
		case 'A':
			column = 0;
			break;
		case 'B':
			column = 1;
			break;
		case 'C':
			column = 2;
			break;
		case 'D':
			column = 3;
			break;
		case 'E':
			column = 4;
			break;
		case 'F':
			column = 5;
			break;
		case 'G':
			column = 6;
			break;
		case 'H':
			column = 7;
			break;
		default:
			column= -1;
			break;
		}
		if(row >= 0 && column >= 0)
		{
			index = (column * 7) + row;
			ToggleButton house = (ToggleButton) grid.getChildAt(index);
			Canvas canvas = new Canvas();
			Paint paint = new Paint(); 
			int max_x = house.getWidth()-1; 
			int max_y = house.getHeight()-1;  
			paint.setColor(Color.RED); 
			paint.setStrokeWidth(1); 
			canvas.drawLine(0, 0, max_x, max_y, paint); 
			canvas.drawLine(0, max_y, max_x, 0, paint); 
			house.draw(canvas);
		}
	}
	
	void disableBoard()
	{
		GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
		for(int i = 0; i < grid.getChildCount(); i++)
		{
			((ToggleButton) grid.getChildAt(i)).setClickable(false);
		}
		RelativeLayout attackButtonGrid = (RelativeLayout) findViewById(R.id.attackButtonGrid);
		for(int i = 0; i < attackButtonGrid.getChildCount(); i++)
		{
			((Button) attackButtonGrid.getChildAt(i)).setClickable(false);
		}
	}
	
	void enableBoard()
	{
		GridLayout grid = (GridLayout) findViewById(R.id.rightGrid);
		for(int i = 0; i < grid.getChildCount(); i++)
		{
			if(!pastAttacks[i])
			{
				((ToggleButton) grid.getChildAt(i)).setClickable(true);
			}
		}
		RelativeLayout attackButtonGrid = (RelativeLayout) findViewById(R.id.attackButtonGrid);
		for(int i = 0; i < attackButtonGrid.getChildCount(); i++)
		{
			((Button) attackButtonGrid.getChildAt(i)).setClickable(true);
		}
	}
	
	public void onAttackSelection(View view)
	{
		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId())
		{
		case (R.id.basicAttackRadioButton):
		{
			if (checked)
				attackType = 0;
			break;
		}
		case (R.id.verticalAttackRadioButton):
		{
			if (checked)
				attackType = 1;
			break;
		}
		case (R.id.diagonalAttackBLTRRadioButton):
		{
			if (checked)
				attackType = 2;
			break;
		}
		case (R.id.diagonalAttackTLBR):
		{
			if (checked)
				attackType = 3;
			break;
		}
		case (R.id.tetrisAttackRadioButton):
		{
			if (checked)
				attackType = 4;
			break;
		}
		case (R.id.xAttackRadioButton):
		{
			if (checked)
				attackType = 5;
			break;
		}
		case (R.id.plusAttackRadioButton):
		{
			if (checked)
				attackType = 6;
			break;
		}
		case (R.id.chooseFourAttackRadioButton7):
		{
			if (checked)
				attackType = 7;
			break;
		}
		case (R.id.squareAttackRadioButton):
		{
			if (checked)
				attackType = 8;
			break;
		}
		default:
			attackType = 0;
		}
	}
	
  /*public void onRotateButtonClick(View view)
  {
	  try
	  {
	  ImageView imageView = selectedImageView;

	    //Decode Image using Bitmap factory.
	    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

	    //Create object of new Matrix.
	    Matrix matrix = new Matrix();

	    //set image rotation value to 90 degrees in matrix.
	    matrix.postRotate(90);

	    //Create bitmap with new values.
	    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	    //put rotated image in ImageView.
	    imageView.setImageBitmap(rotatedBitmap);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
  }
  */

  /*private final class MyTouchListener implements OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
      if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
    	  selectedImageView = (ImageView) view;
    	  ClipData data = ClipData.newPlainText("", "");
    	  DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
    	  view.startDrag(data, shadowBuilder, view, 0);
    	  view.setVisibility(View.INVISIBLE);
    	  return true;
      } else {
    	  return false;
      }
    }
  }*/
}