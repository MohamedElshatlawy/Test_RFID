package com.example.refid_test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.zebra.rfid.api3.*;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class RfidOperations
{
    public static Readers readers;
    public static ArrayList availableRFIDReaderList;
    public static ReaderDevice readerDevice;
    public static RFIDReader reader;
    public static String TAG = "DEMO";
    public static String TEST_TAG="RFID_TAG_TEST";
    public EventHandler eventHandler;
    public Context context;
    public FlutterEngine flutterEngine;
    public String CHANNEL;
    public RfidOperations(Context ctx,FlutterEngine fE,String channel) {
        this.context=ctx;
        this.flutterEngine=fE;
        this.CHANNEL=channel;

        Log.d(TEST_TAG,"Inside Constructor");

            readers = new Readers(context, ENUM_TRANSPORT.BLUETOOTH);

        Log.d(TEST_TAG,readers.toString());
        try {
            Log.d(TEST_TAG,readers.GetAvailableRFIDReaderList().toString());
        } catch (InvalidUsageException e) {
            throw new RuntimeException(e);
        }
        Log.d(TEST_TAG,"Before Starting Async Task");

        new AsyncTask() {

            @Override
            protected Boolean doInBackground(Object[] objects) {
                try {
                    Log.d(TEST_TAG,"Inside doInBackground");

                    if (readers != null) {
                        if (readers.GetAvailableRFIDReaderList() != null) {
                            availableRFIDReaderList = readers.GetAvailableRFIDReaderList();
                            if (availableRFIDReaderList.size() != 0) {
                                // get first reader from list
                                readerDevice = (ReaderDevice) availableRFIDReaderList.get(0);
                                reader = readerDevice.getRFIDReader();
                                if (!reader.isConnected()) {
                                    // Establish connection to the RFID Reader
                                    reader.connect();
                                    Log.d(TEST_TAG,"Reader Connected");
                                    ConfigureReader();
                                    return true;
                                }
                            }
                        }
                    }
                } catch (InvalidUsageException e) {
                    e.printStackTrace();
                } catch (OperationFailureException e) {
                    e.printStackTrace();
                    Log.d(TAG, "OperationFailureException " + e.getVendorMessage());

                }
                Log.d(TEST_TAG,"Null Readers");
                return false;
            }


        }.execute();

    }

    private void ConfigureReader() {
        if (reader.isConnected()) {
            Log.d(TEST_TAG,"Inside Configure Reader");
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
            try {
                // receive events from reader
//                if (eventHandler == null){
//                    Log.d(TEST_TAG,"Null Event Handler");
//
//                    eventHandler = new EventHandler();
//
//                }


                reader.Events.addEventsListener(new RfidEventsListener() {
                    @Override
                    public void eventReadNotify(RfidReadEvents rfidReadEvents) {
                        // Recommended to use new method getReadTagsEx for better performance in case of large tag population
                        Log.d(RfidOperations.TEST_TAG, "Inside Event Read Notify");

                        TagData[] myTags = RfidOperations.reader.Actions.getReadTags(100);
                        if (myTags != null) {
                            for (int index = 0; index < myTags.length; index++) {
                                Log.d(RfidOperations.TAG, "Tag ID " + myTags[index].getTagID());

                                MethodChannel methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);
                                final int finalIndex = index;
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        methodChannel.invokeMethod("getRFIDFromNative",  myTags[finalIndex].getTagID());
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
                        Log.d(RfidOperations.TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
                        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                                new AsyncTask() {


                                    @Override
                                    protected Void doInBackground(Object[] objects) {
                                        try {
                                            RfidOperations. reader.Actions.Inventory.perform();
                                        } catch (InvalidUsageException e) {
                                            e.printStackTrace();
                                        } catch (OperationFailureException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                }.execute();
                            }
                            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                                new AsyncTask() {
                                    @Override
                                    protected Void doInBackground(Object[] objects) {
                                        try {
                                            RfidOperations.  reader.Actions.Inventory.stop();
                                        } catch (InvalidUsageException e) {
                                            e.printStackTrace();
                                        } catch (OperationFailureException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }
                                }.execute();
                            }
                        }
                    }

                });
                // HH event
                reader.Events.setHandheldEvent(true);
                // tag event with tag data
                reader.Events.setTagReadEvent(true);

                // application will collect tag using getReadTags API
                reader.Events.setAttachTagDataWithReadEvent(false);
                // set trigger mode as rfid so scanner beam will not come
                reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);
                // set start and stop triggers
                reader.Config.setStartTrigger(triggerInfo.StartTrigger);
                reader.Config.setStopTrigger(triggerInfo.StopTrigger);
            } catch (InvalidUsageException e) {
                e.printStackTrace();
            } catch (OperationFailureException e) {
                e.printStackTrace();
            }
        }


    }

}


