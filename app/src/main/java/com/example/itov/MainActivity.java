package com.example.itov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    FFmpeg fFmpeg;
    private static int RESULT_LOAD_IMAGE = 1;
    private final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        try {
            loadFFMpegLibrary();

            executeCommand(new String[]{
                    "-f", "concat", "-safe", "0", "-i", "/storage/emulated/0/Download/input.txt",
                    "-i", "/storage/emulated/0/Download/Pal - (amlijatt.in).mp3", "-c:a", "copy", "-vf", "fps=2", "-s",
                    "480x360", "/storage/emulated/0/Download/out.mp4"
            });

        } catch (FFmpegNotSupportedException | FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
//        select();

//        try {
//            make_input_file();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void make_input_file() throws IOException {
        String[] data = new String[]{"file '/storage/emulated/0/Pictures/Instagram/IMG_20200314_155825_839.jpg'", "duration 5",
                "file '/storage/emulated/0/Pictures/Instagram/IMG_20200314_155825_839.jpg'",
                "file '/storage/emulated/0/DCIM/Camera/IMG20200331114433.jpg'",
                "duration 10",
                "file '/storage/emulated/0/DCIM/Camera/IMG20200331114433.jpg'",
                "file '/storage/emulated/0/Pictures/Instagram/IMG_20200314_155904_077.jpg'",
                "duration 7",
                "file '/storage/emulated/0/Pictures/Instagram/IMG_20200314_155904_077.jpg'",
                "file '/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200419-WA0010.jpg'",
                "duration 15",
                "file '/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200419-WA0010.jpg'"};

        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String fileName = "input.txt";

        File f = new File(baseDir + File.separator + fileName);

        FileWriter writer = new FileWriter(f);
        for (String str : data) {
            writer.append(str + "\n");
        }
        writer.flush();
        writer.close();

        Log.i(TAG, "Written!!! ");
        Log.i(TAG, baseDir);
    }

    public void select() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.i(TAG, "1.-->" + selectedImage);

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            for (String path : filePathColumn) {
                Log.i(TAG, "2.-->" + path);
            }

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.i(TAG, "3.-->" + picturePath);

        }
    }

    public void loadFFMpegLibrary() throws FFmpegNotSupportedException {
        if (fFmpeg == null) {
            fFmpeg = FFmpeg.getInstance(this);

            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i(TAG, "Lib Start!!! ");

                }

                @Override
                public void onFinish() {
                    Log.i(TAG, "Lib Fninhd!!! ");

                }

                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "Loaded Failed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Loading Successfully!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void executeCommand(final String[] command) throws FFmpegCommandAlreadyRunningException {
        fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
                Log.i(TAG, "success");

            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);
                Log.i(TAG, "Runnung");

            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
                Log.i(TAG, message);

            }

            @Override
            public void onStart() {
                super.onStart();
                Log.i(TAG, "Start ");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.i(TAG, "Done");
            }
        });
    }

}


//  /storage/emulated/0/Pictures/Instagram/IMG_20200314_155825_839.jpg
//  /storage/emulated/0/Pictures/Instagram/IMG_20200314_155904_077.jpg
//  /storage/emulated/0/DCIM/Facebook/IMG_20191214_115004.jpg
//  /storage/emulated/0/Download
//
//  /storage/emulated/0/DCIM/Camera/IMG20200331114433.jpg
//  /storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200419-WA0010.jpg


//    void convert() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
//
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
//        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
//
//        SeekableByteChannel out = null;
//        try {
//            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//            out = NIOUtils.writableFileChannel(path + "/output.mp4");
//            // for Android use: AndroidSequenceEncoder
//            Rational fps = new Rational(2, 1);
//            AndroidSequenceEncoder encoder = new AndroidSequenceEncoder(out, fps);
//            for (int i = 0; i < 4; i++) {
//                // Generate the image, for Android use Bitmap
//                if(i % 2 == 0) {
//                    encoder.encodeImage(bitmap1);
//                } else {
//                    encoder.encodeImage(bitmap2);
//                }
//            }
//            // Finalize the encoding, i.e. clear the buffers, write the header, etc.
//            encoder.finish();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            NIOUtils.closeQuietly(out);
//            Toast.makeText(this, "Converted...", Toast.LENGTH_SHORT).show();
//        }
//    }