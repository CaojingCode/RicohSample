package com.jjw.ricohlibrary.view;

/**
 * Created by Caojing on 2019/5/14.
 * 你不是一个人在战斗
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import com.blankj.utilcode.util.ToastUtils;
import org.apache.http.params.CoreConnectionPNames;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.Log;

/**
 * 该类继承了DataInputStream实现了Serializable接口
 * 1. 实例化流,获取初始化流和关闭实例流的方法
 * 2. 一个构造函数
 * 3. 一个根据帧数据大小获得位图方法
 */
public class MjpegInputStream extends DataInputStream {
//    /**
//     *
//     */
//    private static final long serialVersionUID = 1L;
//    private final static int HEADER_MAX_LENGTH = 100;
//    //    private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
//    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
//    private static MjpegInputStream mis = null;
//    /**
//     * 用UE打开发现 每一个jpg格式的图片 开始两字节都是 0xFF,0xD8
//     */
//    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
//    /**
//     * 表示服务器发给客户端的一帧数据的长度
//     */
//    private final String CONTENT_LENGTH = "Content-Length";
//    private int mContentLength = -1;
//
//    private MjpegInputStream(InputStream in) {
//        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
//    }
//
//    /**
//     * 调用该类的构造方法 创建MjpegInputStream流
//     *
//     * @param is
//     */
//    public static MjpegInputStream initInstance(InputStream is) {
//        if (mis == null)
//            mis = new MjpegInputStream(is);
//        return mis;
//    }
//
//    /**
//     * 获得创建的mjpegInputsteam流
//     *
//     * @return
//     */
//    public static MjpegInputStream getInstance() {
//        if (mis != null)
//            return mis;
//
//        return null;
//    }
//
//    /**
//     * 因为mpjeginputstream继承了datainputstream
//     * 所以可以调用mpjeginputstream的关闭流方法
//     */
//    public static void closeInstance() {
//        try {
//            if (mis!=null)
//            mis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mis = null;
//    }
//
//    /**
//     * 在数据流里面找SOI_MARKER={(byte)0xFF,(byte) 0xD8}
//     * 所有对IO流的操作都会抛出异常
//     *
//     * @param in
//     * @param sequence
//     * @return
//     * @throws IOException
//     */
//    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence)
//            throws IOException {
//        int seqIndex = 0;
//        byte c;
//        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {// 0 1 2 3
//            c = (byte) in.readUnsignedByte();
//            if (c == sequence[seqIndex]) {
//                seqIndex++;
//                if (seqIndex == sequence.length)//2
//                    return i + 1;//3
//            } else
//                seqIndex = 0;
//        }
//        return -1;
//    }
//
//    /**
//     * 此方法功能是找到索引0xFF,0XD8在字符流的位置
//     * 整个数据流形式：http头信息 帧头(0xFF 0xD8) 帧数据 帧尾(0xFF 0xD9)
//     * 1、首先通过0xFF 0xD8找到帧头位置
//     * 2、帧头位置前的数据就是http头，里面包含Content-Length，这个字段指示了整个帧数据的长度
//     * 3、帧头位置后面的数据就是帧图像的开始位置
//     *
//     * @param in
//     * @param sequence
//     * @return
//     * @throws IOException
//     */
//    private int getStartOfSequence(DataInputStream in, byte[] sequence)
//            throws IOException {
//        int end = getEndOfSeqeunce(in, sequence);
//        return (end < 0) ? (-1) : (end - sequence.length);
//    }
//
//    /**
//     * 从http的头信息中获取Content-Length，知道一帧数据的长度
//     *
//     * @param headerBytes
//     * @return
//     * @throws IOException
//     */
//    private int parseContentLength(byte[] headerBytes) throws IOException,
//            NumberFormatException {
//        /**
//         * 根据字节流创建ByteArrayInputStream流
//         * Properties是java.util包里的一个类，它有带参数和不带参数的构造方法，表示创建无默认值和有默认值的属性列表
//         * 根据流中的http头信息生成属性文件，然后找到属性文件CONTENT_LENGTH的value，这就找到了要获得的帧数据大小
//         * 创建一个 ByteArrayInputStream，使用 headerBytes作为其缓冲区数组
//         */
//        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
//        Properties props = new Properties();/*创建一个无默认值的空属性列表*/
//        props.load(headerIn);/*从输入流中生成属性列表（键和元素对）。*/
//        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));/*用指定的键在此属性列表中搜索属性。*/
//    }
//
//    /**
//     * @return
//     * @throws IOException
//     */
//    public Bitmap readMjpegFrame() throws IOException {
//        mark(FRAME_MAX_LENGTH);/*流中当前的标记位置*/
//        int headerLen = getStartOfSequence(this, SOI_MARKER);
//        reset();/*将缓冲区的位置重置为标记位置*/
//        if (headerLen < 0)
//            return null;
//        byte[] header = new byte[headerLen];
//
//        readFully(header);/*会一直阻塞等待，直到数据全部到达(数据缓冲区装满)*/
////        String s = new String(header);
//        try {
//            mContentLength = parseContentLength(header);// ?
//            ToastUtils.showShort(mContentLength + "");
//        } catch (NumberFormatException e) {
//            return null;
//        }
//        /**
//         * 根据帧数据的大小创建字节数组
//         */
//        byte[] frameData = new byte[mContentLength];
//        readFully(frameData);
//        /**
//         * 根据不同的源(file，stream，byte-arrays)创建位图
//         * 把输入字节流流转为位图
//         */
//        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
//    }

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final static String CONTENT_LENGTH = "Content-Length";
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;

    /**
     * Constructor
     * @param inputStream Input stream for receiving data
     */
    public MjpegInputStream(InputStream inputStream) {
        super(new BufferedInputStream(inputStream, FRAME_MAX_LENGTH));
    }

    /**
     * Acquire end position of specified character string
     * @param dataInputStream Input stream for receiving data
     * @param sequence Specified character string
     * @return End position of specified character string
     * @throws IOException
     */
    private int getEndOfSequence(DataInputStream dataInputStream, byte[] sequence) throws IOException {
        int sequenceIndex = 0;
        byte readByteData;
        for(int index = 0; index < FRAME_MAX_LENGTH; index++) {
            readByteData = (byte) dataInputStream.readUnsignedByte();
            if(readByteData == sequence[sequenceIndex]) {
                sequenceIndex++;
                if(sequenceIndex == sequence.length) {
                    return index + 1;
                }
            } else {
                sequenceIndex = 0;
            }
        }
        return -1;
    }

    /**
     * Acquire start position of specified character string
     * @param dataInputStream Input stream for receiving data
     * @param sequence Specified character string
     * @return Start position of specified character string
     * @throws IOException
     */
    private int getStartOfSequence(DataInputStream dataInputStream, byte[] sequence) throws IOException {
        int endIndex = getEndOfSequence(dataInputStream, sequence);
        return (endIndex < 0) ? (-1) : (endIndex - sequence.length);
    }

    /**
     * Acquire data length from header
     * @param headerByteData Header data
     * @return Data length
     * @throws IOException
     * @throws NumberFormatException
     */
    private int parseContentLength(byte[] headerByteData) throws IOException, NumberFormatException {
        ByteArrayInputStream bais = new ByteArrayInputStream(headerByteData);
        Properties properties = new Properties();
        properties.load(bais);
        return Integer.parseInt(properties.getProperty(CONTENT_LENGTH));
    }

    /**
     * Acquire image data for 1 frame
     * @return Image data for 1 frame
     * @throws IOException
     */
    public Bitmap readMJpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLength = getStartOfSequence(this, SOI_MARKER);
        int contentLength;
        reset();

        byte[] headerData = new byte[headerLength];
        readFully(headerData);
        try {
            contentLength = parseContentLength(headerData);
        } catch (NumberFormatException e) {
            e.getStackTrace();
            contentLength = getEndOfSequence(this, EOF_MARKER);
        }
        reset();

        byte[] frameData = new byte[contentLength];
        skipBytes(headerLength);
        readFully(frameData);

        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
    }
}