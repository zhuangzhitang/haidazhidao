package com.jkteam.zhidao.util;

import java.util.ArrayList;
import java.util.List;

import com.jkteam.zhidao.domain.Course;

/**
 * 获取空余教室
 * @author ZheTang
 * @date 2015-5-18
 *
 */
public class FreeCourse {
	private  int[][] zhuLou = new int[9][26];  //主楼 教室  默认 0为空 ，1 为忙
	private  int[][] zhonghailou = new int[7][37];//钟海楼教室  默认 0为空 ，1 为忙
	
	public FreeCourse (List<Course> list){
		for(int i=0;i<9;i++){
			for (int j = 0; j < 26; j++) {
				zhuLou[i][j]=0;
			}
		}
		
		for(int i=0;i<7;i++){
			for (int j = 0; j < 37; j++) {
				zhonghailou[i][j]=0;
			}
		}
		this.init(list);
	}
	
	/**
	 * 
	 * @param list : 不是空余的教室集合
	 */
	private void init(List<Course> list){
			
			for (Course c:list) {
				String address = c.getAddress();
				if(address.contains("钟海楼")){
					String temp = address.substring(3);//除去“钟海楼”
					int loucen = Integer.parseInt(temp.substring(1,2));//楼层
					int num = Integer.parseInt(temp.substring(3,5));//教室号
					if(loucen>6) continue;
					if(num>25) continue;
					zhonghailou[loucen][num] = 1;
					//System.out.println("钟海楼 "+ "楼层"+loucen+"教室号："+num);
				}else if(address.contains("主楼")){
					String temp = address.substring(2);//除去“主楼”
					int loucen = Integer.parseInt(temp.substring(0,1));//楼层
					int num = Integer.parseInt(temp.substring(1,3));//教室号
					if(loucen>7) continue;
					if(num>36) continue;
					zhuLou[loucen][num] = 1;
				//	System.out.println("主楼 "+ "楼层"+loucen+"教室号："+num);
				}
			}
			
		}
		
		/**
		 * 获取主楼的空余教室
		 * @return
		 */
		public List<String> getZhulouFreeCourse(){
			List<String> freeCourse = new ArrayList<String>();
				
				for (int i = 2; i < 9; i++) {    //获取主楼空余教室
					for (int j = 1; j < 26; j++) {
						if(zhuLou[i][j]!=1){
							int nums = i*100+j;
							String className = "主楼"+nums;
							freeCourse.add(className);
							//System.out.println("主楼 "+ "楼层"+i+"教室号："+j);
						}
					}
				}
			
			return freeCourse;
		}
		
		/**
		 * 获取钟海楼空余课室
		 * @return 钟海楼的空余课室名
		 */
		public List<String> getZhonghailouFreeCourse(){
			List<String> freeCourse = new ArrayList<String>();
			
			for (int i = 2; i < 7; i++) {   
				for (int j = 1; j < 37; j++) {
					if(zhonghailou[i][j]!=1){
						int nums = i*1000+j;
						String className = "钟海楼0"+nums;
						freeCourse.add(className);
					}
				}
			}
		
		  return freeCourse;
	  }
}
