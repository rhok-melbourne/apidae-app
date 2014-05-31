package com.example.apidae;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class CreateStoryActivity extends Activity{
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 1899;
	final static int REQUESTCODE_RECORDING = 12123;
	Uri audioSavedUri;
	MediaPlayer audioPlayer;
    private static final int TAGS_ADD = 1877;
	
	public static String NAME = "Name";
	public static String TAGS_LIST = "TagsList";
	
	
	EditText storyNameField;
	LinearLayout imageScrollLayout;
	LinearLayout audioScrollLayout;
	LinearLayout tagScrollLayout;

	ImageButton imageAddBtn;
	ImageButton audioAddBtn;
	ImageButton tagAddBtn;
	
	//Tag[] tags;
	ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_story);
		
		Bundle data = getIntent().getExtras();
		if(data != null){
			String[] tags = data.getStringArray(TAGS_LIST);
			String storyName = data.getString(NAME);
		}

		imageScrollLayout = (LinearLayout) findViewById(R.id.photoScroll);
		audioScrollLayout = (LinearLayout) findViewById(R.id.audioScroll);
		tagScrollLayout = (LinearLayout) findViewById(R.id.tagScroll);

		imageAddBtn = (ImageButton)findViewById(R.id.photoImageButton);
		audioAddBtn = (ImageButton)findViewById(R.id.audioImageButton);
		tagAddBtn = (ImageButton)findViewById(R.id.tagImageButton);
	}
	
	public void onAddTagsClick(View v){
		Intent cameraIntent = new Intent(this, TagSelect.class); 
        startActivityForResult(cameraIntent, TAGS_ADD); 
	}
	
	public void onAddAudioClick(View v){
		Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		startActivityForResult(recordIntent, REQUESTCODE_RECORDING);
	}
	
	public void onAddPhotoClick(View v){
		
		final CharSequence[] items = { "Take Photo", "Cancel" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Photo");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
	                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	
	private void updateImageScroll(){
		imageScrollLayout.removeAllViews();
		for( Bitmap b : photos){
			ImageView picB = new ImageView(this);
			picB.setImageBitmap(b);
			picB.setScaleType(ScaleType.FIT_CENTER);
			LayoutParams lp = new LayoutParams((int)(b.getWidth()*2), LayoutParams.MATCH_PARENT, 1);
			imageScrollLayout.addView(picB, lp);
		}
		if(photos.size()>0){
			imageAddBtn.setImageResource(R.drawable.cam_button_pressed);
		}else{
			imageAddBtn.setImageResource(R.drawable.cam_button_unpressed);
		}
	}
	
	private void updateAudioScroll(){
		audioScrollLayout.removeAllViews();
		ImageButton iB = new ImageButton(this);
		iB.setImageResource(R.drawable.audio_button_unpressed);
		iB.setScaleType(ScaleType.FIT_CENTER);
		iB.setPadding(10, 10, 10, 10);
		iB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null == audioPlayer){
					audioPlayer = new MediaPlayer();
					audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					
					try {
						audioPlayer.setDataSource(getApplicationContext(), audioSavedUri);
					} catch (IllegalArgumentException e) {
						Toast.makeText(getApplicationContext(), "Illegal Argument", Toast.LENGTH_LONG).show();
					} catch (SecurityException e) {
						Toast.makeText(getApplicationContext(), "Security", Toast.LENGTH_LONG).show();
					} catch (IllegalStateException e) {
						Toast.makeText(getApplicationContext(), "Illegal State", Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						audioPlayer.prepare();
					} catch (IllegalStateException e) {
						Toast.makeText(getApplicationContext(), "Illegal State", Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
					}
					audioPlayer.start();
				}else{
					if(audioPlayer.isPlaying()){
						audioPlayer.stop();
					}else{
						try {
							audioPlayer.setDataSource(getApplicationContext(), audioSavedUri);
						} catch (IllegalArgumentException e) {
							Toast.makeText(getApplicationContext(), "Illegal Argument", Toast.LENGTH_LONG).show();
						} catch (SecurityException e) {
							Toast.makeText(getApplicationContext(), "Security", Toast.LENGTH_LONG).show();
						} catch (IllegalStateException e) {
							Toast.makeText(getApplicationContext(), "Illegal State", Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							audioPlayer.prepare();
						} catch (IllegalStateException e) {
							Toast.makeText(getApplicationContext(), "Illegal State", Toast.LENGTH_LONG).show();
						} catch (IOException e) {
							Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
						}
						audioPlayer.start();
					}
				}
			}
		});
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
		audioScrollLayout.addView(iB, lp);
	}
	
	private void updateTagScroll(ArrayList<Integer> tags){
		tagScrollLayout.removeAllViews();
		for(Integer rID : tags){
			ImageView tIV = new ImageView(this);
			tIV.setImageResource(rID);
			tIV.setScaleType(ScaleType.FIT_CENTER);
			tIV.setPadding(10, 10, 10, 10);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
			tagScrollLayout.addView(tIV, lp);
		}
		if(tags.size()>0){
			tagAddBtn.setImageResource(R.drawable.tag_button_pressed);
		}else{
			tagAddBtn.setImageResource(R.drawable.tag_button_unpressed);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {  
		super.onActivityResult(requestCode, resultCode, intent); 
		switch(requestCode) {
			case CAMERA_REQUEST:  
			    if(resultCode == RESULT_OK){ 
		            Bitmap photo = (Bitmap) intent.getExtras().get("data"); 
		            if(null != photo)
		            	photos.add(photo);
			    }
            	break;
            	
			case TAGS_ADD:
				if(resultCode == RESULT_OK){
					ArrayList<Integer> tags = intent.getExtras().getIntegerArrayList(TAGS_LIST);
					if(null != tags)
						updateTagScroll(tags);
				}
				break;
				
			case REQUESTCODE_RECORDING:
				if(resultCode == RESULT_OK){
					audioSavedUri = intent.getData();
					Toast.makeText(getApplicationContext(), "Audio Saved here: "+audioSavedUri.getPath(), Toast.LENGTH_LONG).show();
					updateAudioScroll();
				}else{
					Toast.makeText(getApplicationContext(), "Problem in saving audio", Toast.LENGTH_LONG).show();
				}
				break;
		}
		updateImageScroll();
    } 
}
