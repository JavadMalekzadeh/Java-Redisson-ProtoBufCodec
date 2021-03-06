package com.jmp.redissonEvaluation;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.FstCodec;
import org.redisson.codec.MarshallingCodec;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ProtobufCodec2 extends BaseCodec {
    private Codec innerCodec=new MarshallingCodec();
    private int BYTES_INT= Integer.BYTES;
    private byte[] EMPTY_INT_BYTE_ARRAY=new byte[BYTES_INT];

    private final Encoder encoder=new Encoder() {
        @Override
        public ByteBuf encode(Object o) throws IOException {
            return encoderHelper(o);
        }
    };

    private final Decoder<Object> decoder=new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf byteBuf, State state) throws IOException {
            try {
                return decoderHelper(byteBuf,state);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    };



    @Override
    public Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return this.encoder;
    }

    private ByteBuf encoderHelper(Object obj) throws IOException {
        if (obj instanceof GeneratedMessageV3) {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            ByteBufOutputStream stream = new ByteBufOutputStream(out);

            byte[] className = ((GeneratedMessageV3) obj)
                    .getClass().getName()
                    .getBytes(StandardCharsets.UTF_8);

            byte[] classNameSize = ByteBuffer.allocate(BYTES_INT)
                    .putInt(className.length).array();

            byte[] bytes = ((GeneratedMessageV3) obj).toByteArray();

            stream.write(classNameSize);
            stream.write(className);
            stream.write(bytes);
            return out;
        }
        else{
            return Unpooled.wrappedBuffer(
                    Unpooled.wrappedBuffer(EMPTY_INT_BYTE_ARRAY),
                    innerCodec.getValueEncoder().encode(obj)
            );
        }
    }

    private Object decoderHelper(ByteBuf byteBuf,State state)
            throws IOException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {

        byte[] sizeByteArray=new byte[BYTES_INT];
        byteBuf.readBytes(sizeByteArray);
        int classNameLenght= ByteBuffer.wrap(sizeByteArray).getInt();
        if(classNameLenght==0)
            return innerCodec.getValueDecoder()
                    .decode(Unpooled.wrappedBuffer(byteBuf),state);
        else
        {
            byte[] classNameByteArray=new byte[classNameLenght];
            byteBuf.readBytes(classNameByteArray);
            String className=
                    new String(classNameByteArray, StandardCharsets.UTF_8);

            Method newBuildMethod=Class.forName(className).getDeclaredMethod("newBuilder");
            GeneratedMessageV3.Builder protoObjBuilder=
                    (GeneratedMessageV3.Builder) newBuildMethod.invoke(null);
            byte[] messageByteArray=new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(messageByteArray);
            return  protoObjBuilder.mergeFrom(messageByteArray).build();

        }
    }


}
