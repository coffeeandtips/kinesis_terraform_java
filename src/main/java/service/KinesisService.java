package service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import model.Event;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KinesisService {

   static String PROVIDER = "AWS KINESIS FIREHOSE";
   static String BLOG = "Coffee and Tips";
   static String KNS_DELIVERY_NAME = "kns-delivery-event";
   static String RECORD_ID = "Record ID ";
   static String EVENT = "Event ";

   public static AmazonKinesisFirehose kinesisFirehoseClient(){
       AmazonKinesisFirehose amazonKinesisFirehose =
               AmazonKinesisFirehoseClient.builder()
                       .withRegion(Regions.US_EAST_1.getName())
                       .build();
       return amazonKinesisFirehose;
   }

    @SneakyThrows
    public static void sendDataWithPutRecordBatch(int maxRecords){

        PutRecordBatchRequest putRecordBatchRequest =
                new PutRecordBatchRequest();

        putRecordBatchRequest.setDeliveryStreamName(KNS_DELIVERY_NAME);

        String line = "";
        List<Record> records = new ArrayList<>();

        while(maxRecords > 0){
            line = getData();
            String data = line + "\n";
            System.out.println(data);
            Record record = new Record()
                    .withData(ByteBuffer.wrap(data.getBytes()));
            records.add(record);
            maxRecords --;
        }

        putRecordBatchRequest.setRecords(records);
        PutRecordBatchResult putRecordResult =
                kinesisFirehoseClient()
                .putRecordBatch(putRecordBatchRequest);

        putRecordResult
                .getRequestResponses()
                .forEach(result -> System.out
                        .println(RECORD_ID + result.getRecordId()));

    }

    @SneakyThrows
    public static void sendDataWithPutRecord(int maxRecords){

            PutRecordRequest PutRecordRequest =
                    new PutRecordRequest();
            PutRecordRequest
                    .setDeliveryStreamName(KNS_DELIVERY_NAME);

            String line = "";

            while(maxRecords > 0){

                line = getData();
                String data = line + "\n";

                System.out.println(EVENT + data);

                Record record = new Record()
                        .withData(ByteBuffer.wrap(data.getBytes()));
                PutRecordRequest.setRecord(record);

                PutRecordResult putRecordResult = kinesisFirehoseClient()
                        .putRecord(PutRecordRequest);

                System.out.println(RECORD_ID +
                        putRecordResult.getRecordId());

                maxRecords --;
            }
    }

    @SneakyThrows
    public static String getData(){
        Event event = new Event();
        event.setEventId(UUID.randomUUID());
        event.setPostId(UUID.randomUUID());
        event.setBlog(BLOG);
        event.setEventDate(LocalDateTime.now().toString());
        event.setProvider(PROVIDER);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(event);
    }

    public static void main(String[] args) {
        sendDataWithPutRecordBatch(500);
        //sendDataWithPutRecord(2000);
    }

}
