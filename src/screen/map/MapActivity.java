package screen.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;

import screen.chat.ChatActivity;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.*;

import data.chat.ChatMessage;
import data.chat.ChatSummary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;

public class MapActivity extends Activity {

  static HashMap<String, Marker> markerMap = new HashMap<String, Marker>();
  static HashMap<String, ChatSummary> chatSummaryMap = new HashMap<String, ChatSummary>();

  String selectedMarkerId = "";
  boolean selectionAvailable = true;

  static final float triangleScreenSizeX = 0.05f;
  static final float triangleScreenSizeY = (float) (triangleScreenSizeX * Math.sqrt(0.75));

  static final float bubbleUnselectedScreenSizeMin = 0.10f;
  static final float bubbleUnselectedScreenSizeMax = 0.20f;

  static final float bubbleSelectedScreenSizeX = 0.67f;
  static final float bubbleSelectedScreenSizeY = 0.33f;

  final int MARKER_UPDATE_INTERVAL = 2000; /* milliseconds */
  Handler handler = new Handler();

  Runnable updateMarker = new Runnable() {
      @Override
      public void run() {
        selectionAvailable = false;

        Marker marker = markerMap.get(selectedMarkerId);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        long currTime;

        do {
          try {
            Thread.sleep(33);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          currTime = Math.min(System.currentTimeMillis(), endTime);
          float percentElapsed = (currTime - startTime) / (endTime - startTime);
          
          marker.setPosition(new LatLng(52.1310799, -106.6341388 + (-106.6341388 - -106.6241388) * percentElapsed));
        } while (currTime < endTime);

        selectionAvailable = true;
      }
  };
  
  // TODO:
  ArrayList<ChatSummary> generateChatSummaries() {
    ArrayList<ChatSummary> chatSummaries = new ArrayList<ChatSummary>();
    
    Location location = new Location("");
    location.setLatitude(52.1310799);
    location.setLongitude(-106.6341388);
    
    chatSummaries.add(new ChatSummary(new ChatMessage("Josh Heinrichs", "Josh's ID", "Anyone up for ultimate frisbee?", location, new DateTime()), 40, 40));

    location = new Location("");
    location.setLatitude(52.1310799);
    location.setLongitude(-106.6241388);
    
    chatSummaries.add(new ChatSummary(new ChatMessage("Josh Heinrichs", "Josh's ID", "Anyone up for ultimate frisbee?", location, new DateTime()), 80, 40));

    return chatSummaries;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_activity);
    GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    ArrayList<ChatSummary> chatSummaries = generateChatSummaries();

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.1310799, -106.6341388), 14));

    // draw user view distance
    map.addCircle(new CircleOptions().center(new LatLng(52.1310799, -106.6341388)).radius(1000)
        .strokeColor(Color.argb(60, 255, 40, 50)).strokeWidth(5)
        .fillColor(Color.argb(30, 255, 40, 50)));

    // create markers
    for (int i = 0; i < chatSummaries.size(); i++) {
      ChatSummary chatSummary = chatSummaries.get(i);
      
      LatLng location = new LatLng(chatSummary.chatMessage.location.getLatitude(), chatSummary.chatMessage.location.getLongitude()); 
      
      Marker marker =
          map.addMarker(new MarkerOptions()
              .icon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummary)))
              .anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
              .position(location));
      markerMap.put(marker.getId(), marker);
      chatSummaryMap.put(marker.getId(), chatSummary);
    }

    map.setOnMarkerClickListener(new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        if (selectedMarkerId.equals(marker.getId())) {
          Intent intent = new Intent(MapActivity.this, ChatActivity.class);
          startActivity(intent);
        } else if (selectionAvailable) {
          deselectMarker();
          selectedMarkerId = marker.getId();
          animateMarkerSelection(marker, getWindowManager().getDefaultDisplay());
//          AnimateMarkerSelect animateMarkerSelect = new AnimateMarkerSelect();
//          animateMarkerSelect.doInBackground(marker.getId());
        }        
        return false;
      }

    });

    map.setOnMapClickListener(new OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        deselectMarker();
      }

    });
  }

  void deselectMarker() {
    if (!selectedMarkerId.isEmpty()) {
      AnimateMarkerDeselect animateMarkerDeselected = new AnimateMarkerDeselect();
      animateMarkerDeselected.doInBackground(new String(selectedMarkerId));
      selectedMarkerId = "";
    }
  }

  Bitmap createMarkerIcon(ChatSummary chatSummary) {
    // todo: create function for bubble size

    int numRepliesUnread = chatSummary.numMessages - chatSummary.numMessagesRead;

    float triangleWidth = 30;
    float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

    String strText = Integer.toString(numRepliesUnread);

    Paint paintText = new Paint();
    paintText.setColor(getResources().getColor(R.color.chat_me_foreground));
    paintText.setTextSize(10 + chatSummary.numMessages);
    paintText.setTextAlign(Paint.Align.CENTER);
    paintText.setAntiAlias(true);

    Rect boundsText = new Rect();
    paintText.getTextBounds(strText, 0, strText.length(), boundsText);

    int maxBounds = Math.max(boundsText.width(), boundsText.height());

    Bitmap image =
        Bitmap.createBitmap((int) (maxBounds * 1.75), (int) (maxBounds * 1.75)
            + (int) triangleHeight / 2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(image);

    Point trianglePosition = new Point();
    trianglePosition.x = (int) (image.getWidth() / 2 - triangleWidth / 2);
    trianglePosition.y = (int) (image.getHeight() - triangleHeight);

    Path triangle = new Path();
    triangle.moveTo(trianglePosition.x, trianglePosition.y);
    triangle.lineTo(trianglePosition.x + triangleWidth * 1.0f, trianglePosition.y);
    triangle.lineTo(trianglePosition.x + triangleWidth * 0.5f, trianglePosition.y + triangleHeight);

    Paint paintShape = new Paint();
    paintShape.setColor(getResources().getColor(R.color.chat_me_background));
    paintShape.setAntiAlias(true);

    canvas.drawPath(triangle, paintShape);
    canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2,
        paintShape);
    canvas.drawText(strText, canvas.getWidth() / 2,
        canvas.getWidth() / 2 + boundsText.height() / 2, paintText);

    return image;
  }

  private class AnimateMarkerSelect extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... markerIds) {
      selectionAvailable = false;

      Marker marker = markerMap.get(markerIds[0]);
      ChatSummary chatSummary = chatSummaryMap.get(markerIds[0]);

      long startTime = System.currentTimeMillis();
      long endTime = startTime + 1000;
      long currTime;

      // TODO: Make size relative to screen size, add text, make formula for bubble size
      Display display = getWindowManager().getDefaultDisplay();
      Point screenSize = new Point();
      display.getSize(screenSize);

      // TODO: Instead of using screen.x, use smallest screen dim?
      Point triangleSize =
          new Point((int) (screenSize.x * triangleScreenSizeX),
              (int) (screenSize.x * triangleScreenSizeY));

      Point bubbleSizeInitial =
          new Point((int) (screenSize.x * 0.20 + chatSummary.numMessages),
              (int) (screenSize.x * 0.20 + chatSummary.numMessages));

      do {
        try {
          Thread.sleep(33);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        currTime = Math.min(System.currentTimeMillis(), endTime);
        float percentElapsed = (currTime - startTime) / (endTime - startTime);

//        Point bubbleSize =
//            new Point(100 + (int) ((screenSize.x * 0.67 - 100) * percentElapsed),
//                100 + (int) ((screenSize.x * 0.33 - 100) * percentElapsed));
//
//        Bitmap image =
//            Bitmap.createBitmap(bubbleSize.x, bubbleSize.y + triangleSize.y / 2,
//                Bitmap.Config.ARGB_8888);
//
//        float radius = 50 - 30 * percentElapsed;
//        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
//        RoundRectShape bubble = new RoundRectShape(radii, null, null);
//
//        bubble.resize(bubbleSize.x, bubbleSize.y);
//
//        Point trianglePosition =
//            new Point((int) (image.getWidth() / 2 - triangleSize.x / 2),
//                (int) (image.getHeight() - triangleSize.y));
//
//        Path triangle = new Path();
//        triangle.moveTo(trianglePosition.x, trianglePosition.y);
//        triangle.lineTo(trianglePosition.x + triangleSize.x, trianglePosition.y);
//        triangle.lineTo(trianglePosition.x + triangleSize.x * 0.5f, trianglePosition.y
//            + triangleSize.y);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(getResources().getColor(R.color.chat_me_background));
//
//        Canvas canvas = new Canvas(image);
//        bubble.draw(canvas, paint);
//        canvas.drawPath(triangle, paint);
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(image));
        
        marker.setPosition(new LatLng(52.1310799, -106.6341388 + (-106.6341388 - -106.6241388) * percentElapsed));
      } while (currTime < endTime);

      selectionAvailable = true;
      return null;
    }
  }

  private class AnimateMarkerDeselect extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... markerIds) {
      Marker marker = markerMap.get(selectedMarkerId);
      marker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummaryMap.get(marker
          .getId()))));
      return null;
    }
  }

  // class MyInfoWindowAdapter implements InfoWindowAdapter{
  //
  // private final View myContentsView;
  //
  // MyInfoWindowAdapter(){
  // myContentsView = getLayoutInflater().inflate(R.layout.inbox_item, null);
  // }
  //
  // @Override
  // public View getInfoContents(Marker marker) {
  //
  // return myContentsView;
  // }
  //
  // @Override
  // public View getInfoWindow(Marker marker) {
  // return myContentsView;
  // }
  //
  // }
  
  static void animateMarkerSelection(final Marker marker, final Display display)
  {
    final LatLng startPosition = marker.getPosition();
    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    final float durationInMs = 1000;
    final ChatSummary chatSummary = chatSummaryMap.get(marker.getId());
    
    handler.post(new Runnable()
    {

      long elapsed;
      float t;
      
      @Override
      public void run() {
        elapsed = SystemClock.uptimeMillis() - start;
        t = elapsed / durationInMs;
        t = (float) Math.min(t, 1.0);

        // TODO: Make size relative to screen size, add text, make formula for bubble size
        
        Point screenSize = new Point();
        display.getSize(screenSize);

        // TODO: Instead of using screen.x, use smallest screen dim?
        Point triangleSize =
            new Point((int) (screenSize.x * triangleScreenSizeX),
                (int) (screenSize.x * triangleScreenSizeY));

        Point bubbleSizeInitial =
            new Point((int) (screenSize.x * 0.20 + chatSummary.numMessages),
                (int) (screenSize.x * 0.20 + chatSummary.numMessages));


          Point bubbleSize =
              new Point(100 + (int) ((screenSize.x * 0.67 - 100) * t),
                  100 + (int) ((screenSize.x * 0.33 - 100) * t));
  
          Bitmap image =
              Bitmap.createBitmap(bubbleSize.x, bubbleSize.y + triangleSize.y / 2,
                  Bitmap.Config.ARGB_8888);
  
          float radius = 50 - 30 * t;
          float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
          RoundRectShape bubble = new RoundRectShape(radii, null, null);
  
          bubble.resize(bubbleSize.x, bubbleSize.y);
  
          Point trianglePosition =
              new Point((int) (image.getWidth() / 2 - triangleSize.x / 2),
                  (int) (image.getHeight() - triangleSize.y));
  
          Path triangle = new Path();
          triangle.moveTo(trianglePosition.x, trianglePosition.y);
          triangle.lineTo(trianglePosition.x + triangleSize.x, trianglePosition.y);
          triangle.lineTo(trianglePosition.x + triangleSize.x * 0.5f, 
              trianglePosition.y + triangleSize.y);
  
          Paint paint = new Paint();
          paint.setAntiAlias(false);
          paint.setARGB(255, 255, 65 + (int) (30 * t), 65 + (int) (30 * t));
  
          Canvas canvas = new Canvas(image);
          bubble.draw(canvas, paint);
          canvas.drawPath(triangle, paint);
          marker.setIcon(BitmapDescriptorFactory.fromBitmap(image));
        
        if(t < 1)
        {
          handler.postDelayed(this, 16);
        }
      }
      
    });
  }

}
