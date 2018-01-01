import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 测试usb与pc通信 通过adb端口转发方式
 * 
 * @author chl
 * 
 */
public class testPcClient {

	public static void main(String[] args) throws InterruptedIOException {
		try {
			// adb 指令
			Runtime.getRuntime().exec("adb forward tcp:12580 tcp:18888"); // 端口转换
			Thread.sleep(3000);
		} catch (Exception e) {
			e.printStackTrace();

		}
		Socket socket = null;
		try {
			InetAddress serveraddr = null;
			serveraddr = InetAddress.getByName("127.0.0.1");
			System.out.println("TCP 111111" + "C: Connecting...");
			socket = new Socket(serveraddr, 12580);
			System.out.println("TCP 221122" + "C: Receive");
			BufferedOutputStream out = new BufferedOutputStream(
					socket.getOutputStream());
			BufferedInputStream in = new BufferedInputStream(
					socket.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			boolean flag = true;
			while (flag) {
				System.out.print("请输入数字0进行文件传输,退出输入-1。");
				String strWord = br.readLine();// 从控制台输入
				if(strWord.equals("0")){
					/* 准备接收文件数据 */
//						out.write("service receive OK".getBytes());
					    out.write("0".getBytes());
						out.flush();
						
                    Thread.sleep(300);//等待服务端回复
//                    System.out.println("Start Receive");
					/* 接收文件数据，4字节文件长度，4字节文件格式，其后是文件数据 */
					byte[] filelength = new byte[4];
					byte[] fileformat = new byte[4];
					byte[] filebytes = null;

					/* 从socket流中读取完整文件数据 */
					filebytes = receiveFileFromSocket(in, out, filelength,
							fileformat);
                    System.out.println("文件大小："+(float)filebytes.length/1024.0+"KB");
					try {
						/* 生成文件 */
						File file = FileHelper.newFile("Receive.png");
						FileHelper.writeFile(file, filebytes, 0,
								filebytes.length);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(strWord.equalsIgnoreCase("EXIT")){
					out.write("EXIT".getBytes());  
                    out.flush();  
                    System.out.println("EXIT!");  
                    String strFormsocket = readFromSocket(in);  
                    System.out.println("the data sent by server is:/r/n"  
                            + strFormsocket);  
                    flag = false;  
                    System.out  
                            .println("=============================================");  
				}
			}

		} catch (UnknownHostException e1) {
			System.out.println("TCP 331133" + "ERROR:" + e1.toString());
		} catch (Exception e2) {
			System.out.println("TCP 441144" + "ERROR:" + e2.toString());
		}finally{
			try {  
                if (socket != null) {  
                    socket.close();  
                    System.out.println("socket.close()");  
                }  
            } catch (IOException e) {  
                System.out.println("TCP 551155" + "ERROR:" + e.toString());  
            }  
		}
	}
	/**
	 * 功能：从socket流中读取完整文件数据
	 * 
	 * InputStream in：socket输入流
	 * 
	 * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
	 * 
	 * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
	 * 
	 * */
	public static byte[] receiveFileFromSocket(InputStream in,
			OutputStream out, byte[] filelength, byte[] fileformat) {
		byte[] filebytes = null;// 文件数据
		try {
			in.read(filelength);// 读文件长度
			int filelen = MyUtil.bytesToInt(filelength);// 文件长度从4字节byte[]转成Int
			String strtmp = "read file length ok:" + filelen;
			out.write(strtmp.getBytes("utf-8"));
			out.flush();

			filebytes = new byte[filelen];
			int pos = 0;
			int rcvLen = 0;
			while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0) {
				pos += rcvLen;
			}
			out.write("read file ok".getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filebytes;
	}

	/* 从InputStream流中读数据 */
	public static String readFromSocket(InputStream in) {
		int MAX_BUFFER_BYTES = 4000;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try {
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
}
