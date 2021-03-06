package com.jmp.redissonEvaluation;


import com.example.samples.SampleObjectOuterClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.BitSetCodec;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.AvroJacksonCodec;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;

import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Demo {
    public static void main(String[] args) {

        Config config = new Config();

        config.setCodec(new ProtobufCodec2());
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");

        RedissonClient client = Redisson.create(config);

        RKeys keys = client.getKeys();
        System.out.println(keys.count());

        //		get the keys conforming to a pattern
        Iterable<String> keysByPattern = keys.getKeysByPattern("*key*");
        Iterator<String> iterator = keysByPattern.iterator();

        while (iterator.hasNext()) {
            String next = iterator.next();
            System.out.println(next);
            RBucket<String> bucket = client.getBucket(next, StringCodec.INSTANCE);
            System.out.println(bucket.get());
        }


        //		RsBucket class, this object can hold any type of object
        RBucket<com.example.samples.SampleObject> bucket =
                client.getBucket("sample127");
        //RBucket<SampleObject> bucket =
        //        client.getBucket("sample1223");


        // System.out.println(bucket.size());
        SampleObject sampleObject = new SampleObject();
        sampleObject.setName("javad");
        sampleObject.setFamily("malekzadeh");
        sampleObject.setAge(32);
        sampleObject.setWage(20_000_000.500);
        sampleObject.getSampleObject2s().add(new SampleObject2("tehran"));
        sampleObject.getSampleObject2s().add(new SampleObject2("esfehan"));

        // object proto type
        com.example.samples.SampleObject.Builder message=
                com.example.samples.SampleObject.newBuilder();
        message.setName("javad")
            .setFamily("malekzadeh")
            .setAge(32)
            .setWage(20_000_000.500)
            .addSampleObject2S(
                com.example.samples.SampleObject2.newBuilder().setAddress("tehran"))
                .addSampleObject2S(
                        com.example.samples.SampleObject2.newBuilder().setAddress("esfehan"));

       bucket.set(message.build());
       //bucket.set(sampleObject);


        com.example.samples.SampleObject sample = bucket.get();
        System.out.println(new Date() + "\n\t\t" + sample.getName());
        System.out.println(sample.getFamily());
        System.out.println(sample.getSampleObject2S(0).getAddress());
        System.out.println(sample.getSampleObject2S(1).getAddress());
    }
}
