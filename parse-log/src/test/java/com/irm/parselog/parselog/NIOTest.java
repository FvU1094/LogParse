package com.irm.parselog.parselog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

public class NIOTest {
	@Test
	public void method1(){
        RandomAccessFile aFile = null;
        try{
            aFile = new RandomAccessFile("1.log","rw");
            FileChannel fileChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);  //分配空间
            int bytesRead = fileChannel.read(buf);  //写入数据
            System.out.println(bytesRead);
            while(bytesRead != -1)
            {
                buf.flip();  //调用filp()方法，position重设为0，从写转换为读
                while(buf.hasRemaining())
                {
                    System.out.print((char)buf.get());  //从Buffer中读取数据
                }
                buf.compact();
                bytesRead = fileChannel.read(buf);  //写入数据到Buffer
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile != null){
                    aFile.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
	@Test
	// 将来式：
	public void futureDemo() {
		Path path = Paths.get("D://log//1.log");
		try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(path);) {
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 设缓存
			Future<Integer> result = channel.read(buffer, 0); // 得到返回结果

			while (!result.isDone()) {
				// 在得到IO结果前，主线程期间可以做其他的事情；
				System.out.println("主线程在工作中……");
			}
			Integer bytesRead = result.get();
			System.out.println("read:" + bytesRead);//输出的是最后一次缓冲池中的容量；

		} catch (Exception e) {
		}
	}

	@Test
	// 回调式：
	public void callbackDemo() {
		Path path = Paths.get("D://log//2.log");
		AsynchronousFileChannel channel = null;
		try {
			channel = AsynchronousFileChannel.open(path);
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
			channel.read(buffer, 0, null, new CompletionHandler() {
				@Override
				public void completed(Object result, Object attachment) {
					System.out.println("Success！");
					System.out.println("Bytes Read = " + result); //输出的是最后一次缓冲池中的容量；
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					System.out.println("Fail！");
					System.out.println(exc.getCause());
				}
			});

			System.out.println("主线程在工作中……");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (channel != null) {
					channel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
/*    static void readNIO() {  
         String pathname = "C:\\Users\\adew\\Desktop\\jd-gui.cfg";  
         FileInputStream fin = null;  
         try {  
             fin = new FileInputStream(new File(pathname));  
             FileChannel channel = fin.getChannel();  
   
             int capacity = 100;// 字节  
             ByteBuffer bf = ByteBuffer.allocate(capacity);  
             System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity()  
                     + "位置是：" + bf.position());  
             int length = -1;  
   
             while ((length = channel.read(bf)) != -1) {  
   
                   
                  * 注意，读取后，将位置置为0，将limit置为容量, 以备下次读入到字节缓冲中，从0开始存储  
                    
                 bf.clear();  
                 byte[] bytes = bf.array();  
                 System.out.write(bytes, 0, length);  
                 System.out.println();  
   
                 System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity()  
                         + "位置是：" + bf.position());  
   
             }  
   
             channel.close();  
   
         } catch (FileNotFoundException e) {  
             e.printStackTrace();  
         } catch (IOException e) {  
             e.printStackTrace();  
         } finally {  
             if (fin != null) {  
                 try {  
                     fin.close();  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
             }  
         }  
     }  
   
     static void writeNIO() {  
         String filename = "out.txt";  
         FileOutputStream fos = null;  
         try {  
   
             fos = new FileOutputStream(new File(filename));  
             FileChannel channel = fos.getChannel();  
             ByteBuffer src = Charset.forName("utf8").encode("你好你好你好你好你好");  
             // 字节缓冲的容量和limit会随着数据长度变化，不是固定不变的  
             System.out.println("初始化容量和limit：" + src.capacity() + ","  
                     + src.limit());  
             int length = 0;  
   
             while ((length = channel.write(src)) != 0) {  
                   
                  * 注意，这里不需要clear，将缓冲中的数据写入到通道中后 第二次接着上一次的顺序往下读  
                    
                 System.out.println("写入长度:" + length);  
             }  
   
         } catch (FileNotFoundException e) {  
             e.printStackTrace();  
         } catch (IOException e) {  
             e.printStackTrace();  
         } finally {  
             if (fos != null) {  
                 try {  
                     fos.close();  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
             }  
         }  
     }  
   
     static void testReadAndWriteNIO() {  
         String pathname = "C:\\Users\\adew\\Desktop\\test.txt";  
         FileInputStream fin = null;  
           
         String filename = "test-out.txt";  
         FileOutputStream fos = null;  
         try {  
             fin = new FileInputStream(new File(pathname));  
             FileChannel channel = fin.getChannel();  
   
             int capacity = 100;// 字节  
             ByteBuffer bf = ByteBuffer.allocate(capacity);  
             System.out.println("限制是：" + bf.limit() + "容量是：" + bf.capacity()+ "位置是：" + bf.position());  
             int length = -1;  
   
             fos = new FileOutputStream(new File(filename));  
             FileChannel outchannel = fos.getChannel();  
               
               
             while ((length = channel.read(bf)) != -1) {  
                   
                 //将当前位置置为limit，然后设置当前位置为0，也就是从0到limit这块，都写入到同道中  
                 bf.flip();  
                   
                 int outlength =0;  
                 while((outlength=outchannel.write(bf)) != 0){  
                     System.out.println("读，"+length+"写,"+outlength);  
                 }  
                   
                 //将当前位置置为0，然后设置limit为容量，也就是从0到limit（容量）这块，  
                 //都可以利用，通道读取的数据存储到  
                 //0到limit这块  
                 bf.clear();  
                   
             }  
         } catch (FileNotFoundException e) {  
             e.printStackTrace();  
         } catch (IOException e) {  
             e.printStackTrace();  
         } finally {  
             if (fin != null) {  
                 try {  
                     fin.close();  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
             }  
             if (fos != null) {  
                 try {  
                     fos.close();  
                 } catch (IOException e) {  
                     e.printStackTrace();  
                 }  
			}  
         }  
     }  
   
     @SuppressWarnings("resource")  
     public static void main(String[] args) {  
         testReadAndWriteNIO();  
     }  
*/

}

