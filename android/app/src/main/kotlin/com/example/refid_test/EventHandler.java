package com.example.refid_test;

import android.os.AsyncTask;
import android.util.Log;

import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.TagData;

// Read/Status Notify handler
// Implement the RfidEventsListener class to receive event notifications
public class EventHandler implements RfidEventsListener {

    // Read Event Notification



    public void eventReadNotify(RfidReadEvents e) {
        // Recommended to use new method getReadTagsEx for better performance in case of large tag population
        Log.d(RfidOperations.TEST_TAG, "Inside Event Read Notify");

        TagData[] myTags = RfidOperations.reader.Actions.getReadTags(100);
        if (myTags != null) {
            for (int index = 0; index < myTags.length; index++) {
                Log.d(RfidOperations.TAG, "Tag ID " + myTags[index].getTagID());
                if (myTags[index].getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ && myTags[index].getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                    if (myTags[index].getMemoryBankData().length() > 0) {
                        Log.d(RfidOperations.TAG, " Mem Bank Data " + myTags[index].getMemoryBankData());
                    }
                }
            }
        }
    }

    // Status Event Notification
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



}
