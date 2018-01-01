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
 * ����usb��pcͨ�� ͨ��adb�˿�ת����ʽ
 * 
 * @author chl
 * 
 */
public class testPcClient {

	public static void main(String[] args) throws InterruptedIOException {
		try {
			// adb ָ��
			Runtime.getRuntime().exec("adb forward tcp:12580 tcp:18888"); // �˿�ת��
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
				System.out.print("����������0�����ļ�����,�˳�����-1��");
				String strWord = br.readLine();// �ӿ���̨����
				if(strWord.equals("0")){
					/* ׼�������ļ����� */
//						out.write("service receive OK".getBytes());
					    out.write("0".getBytes());
						out.flush();
						
                    Thread.sleep(300);//�ȴ�����˻ظ�
//                    System.out.println("Start Receive");
					/* �����ļ����ݣ�4�ֽ��ļ����ȣ�4�ֽ��ļ���ʽ��������ļ����� */
					byte[] filelength = new byte[4];
					byte[] fileformat = new byte[4];
					byte[] filebytes = null;

					/* ��socket���ж�ȡ�����ļ����� */
					filebytes = receiveFileFromSocket(in, out, filelength,
							fileformat);
                    System.out.println("�ļ���С��"+(float)filebytes.length/1024.0+"KB");
					try {
						/* �����ļ� */
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
	 * ���ܣ���socket���ж�ȡ�����ļ�����
	 * 
	 * InputStream in��socket������
	 * 
	 * byte[] filelength: ����ǰ4���ֽڴ洢Ҫת�͵��ļ����ֽ���
	 * 
	 * byte[] fileformat������ǰ5-8�ֽڴ洢Ҫת�͵��ļ��ĸ�ʽ����.apk��
	 * 
	 * */
	public static byte[] receiveFileFromSocket(InputStream in,
			OutputStream out, byte[] filelength, byte[] fileformat) {
		byte[] filebytes = null;// �ļ�����
		try {
			in.read(filelength);// ���ļ�����
			int filelen = MyUtil.bytesToInt(filelength);// �ļ����ȴ�4�ֽ�byte[]ת��Int
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

	/* ��InputStream���ж����� */
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
