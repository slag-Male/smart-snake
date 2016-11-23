package guojian.smart_snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 * Title:
 * </p>
 * 
 * @author guojian
 * @date 2016年11月20日 下午2:26:00
 * @email 1181819395@qq.com
 */
public class BFS {

	private static boolean isNear(Point a, Point b) {
		return (Math.abs(a.x - b.x) == 1 && a.y == b.y) || (a.x == b.x && Math.abs(a.y - b.y) == 1);
	}

	/**
	 * 获取二维数组中一个点周围的4个点,并打乱顺序
	 * 
	 * @param array
	 * @param p
	 * @return
	 */
	private static List<Point> get4Point(Point[][] array, Point p) {
		List<Point> sourceList = Arrays.asList(new Point[] { array[p.row + 1][p.col], array[p.row - 1][p.col],
				array[p.row][p.col - 1], array[p.row][p.col + 1] });
		List<Point> list = new ArrayList<>();
		list.addAll(sourceList);
		Collections.shuffle(list, new Random());
		return list;
	}

	/**
	 * 在二维数组中寻找两点间的最短距离
	 * <p>
	 * 1.访问A点,将A点 标记为 访问的点(用一个集合 保存已经访问的点,Set s),判断A点是否是B点 <br>
	 * 2.如果是B，跳出循环,返回路径。<br>
	 * 3.如果不是 ,将 A周围4个点符合要求的点(不是墙，不是蛇身，不是访问的点)的点加入 队列(队列Queue q)。并标记 加入队列的点的
	 * 父节点为A。<br>
	 * 
	 * 5.取出队列q中的一个点。当这个点为A。循环 1235
	 * </p>
	 * 
	 * @return p1到p2的所有点,包含p1和p2。
	 */
	public static Path searchShortPath(Point p1, Point p2, Point[][] mazeArray) {
		Queue<Point> q = new LinkedBlockingQueue<>();// 没访问的点
		Set<Point> s = new HashSet<>();// 已经访问的点
		Point A = (Point) p1.clone();
		Point B = (Point) p2.clone();
		Point[][] array = mazeArray.clone();
		q.add(A);// 初始化，第一个点
		while (!q.isEmpty()) {
			A = q.poll();
			s.add(A);
			if (isNear(A, B)) {
				List<Point> l = new ArrayList<>();
				l.add(B);
				while (true) {
					l.add(A);
					if (A.parent == null) {
						break;
					} else {
						A = A.parent;
					}
				}
				Collections.reverse(l);
				return new Path(l);
			} else {
				List<Point> list = get4Point(array, A);
				for(Point n:list){// A周围4个点，把符合要求的点加入q
					Type type = n.getType();
					if (type != Type.Head && type != Type.Body && type != Type.Tail && type != Type.Wall
							&& !s.contains(n)) {
						n.setParent(A);
						q.add(n);
					}else{
						continue;
					}
				}
			}
		}
		return new Path(null);
	}

	static class Path {
		List<Point> list;
		Path(List<Point> list) {
			if (list == null) {
				this.list = new ArrayList<>();
			} else {
				this.list = list;
			}
		}

		public int size() {
			return list.size();
		}

		public Point getNextPoint() {
			return list.get(1);
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
	}


	public static Path searchLongPath(Point p1, Point p2, Point[][] array) {
		List<Point> list = get4Point(array, p1);
		List<Path> pathList = new ArrayList<>();
		List<Point> fitPointList= new ArrayList<>();
		
		for(int i=0;i<list.size();i++){
			Type type = list.get(i).getType();
			if (type ==Type.Apple|| type==Type.Cell) {
				fitPointList.add(list.get(i));
			}else{
				continue;
			}
		}
		
		for(int i=0;i<fitPointList.size();i++){
			Path searchShortPath = searchShortPath(fitPointList.get(i), p2, array);
			searchShortPath.list.add(0, p1);
			pathList.add(searchShortPath);
		}
		
		Collections.sort(pathList, (o1, o2) -> {
			if (o1.size() > o2.size()) {
				return 1;
			} else if (o1.size() == o2.size()) {
				return 0;
			} else {
				return -1;
			}
		});

		if (pathList.size() > 0) {
			return pathList.get(pathList.size() - 1);
		} else {
			return new Path(null);
		}

	}

	/**
	 * 从尾巴到头部，可以走的最远距离
	 * 
	 * @param snakeHead
	 * @param snake
	 * @param array
	 * @return
	 */
	public static Path searchBodysPath(Point snakeHead, Snake snake, Point[][] array) {
		for (Point p : snake.getList()) {
			Path tempPath = searchShortPath(snakeHead, p, array);
			if (tempPath.isEmpty()) {
				continue;
			} else {
				if (tempPath.size() > 1) {
					return  searchLongPath(snakeHead, p, array);
				} else {
					continue;
				}
			}
		}
		return new Path(null);
	}

	/**
	 * A到B所经过的点的列表,含A点
	 * 
	 * @param p
	 * @return
	 */
	/*
	 * private static List<Point> findPaths(Point p) { List<Point> list = new
	 * ArrayList<>(); while (true) { list.add(p); if (p.parent == null) { break;
	 * } else { p = p.parent; } }
	 * 
	 * Collections.reverse(list); return list; }
	 */

	/*
	 * static class Path { List<Node> list;// 蛇头到 目标 点的 列表,含蛇头
	 * 
	 * Path(List<Node> list) { this.list = list; }
	 * 
	 * public boolean hasPath() { if (list == null) { return false; } else {
	 * return true; } }
	 * 
	 * public Point getNextPoint() { return list.get(1).point; }
	 * 
	 * public int getSize() { if (list == null) { return -1; } else { return
	 * list.size(); } } }
	 */

	/*
	 * static class Node { Node parent; Point point;
	 * 
	 * public Node(Point p1) { this.point = p1; this.parent = null; }
	 * 
	 * public Node(Point p, Node parent) { this.point = p; this.parent = parent;
	 * }
	 * 
	 * public Node getParent() { return parent; }
	 * 
	 * public void setParent(Node parent) { this.parent = parent; }
	 * 
	 * public Point getPoint() { return point; }
	 * 
	 * public void setPoint(Point point) { this.point = point; }
	 * 
	 * @Override public boolean equals(Object obj) { if (((Node)
	 * obj).getPoint().equals(this.point)) { return true; } else { return false;
	 * } }
	 * 
	 * }
	 */

	/*
	 * public static Path findTailShortPath(Point snakeHead, Point snakeTail,
	 * Point[][] array) { q = new LinkedBlockingQueue<>();// 没访问的点 s = new
	 * HashSet<>();// 已经访问的点 A = new Node(snakeHead); B = new Node(snakeTail);
	 * q.add(A);// 初始化，第一个点
	 * 
	 * while (!q.isEmpty()) { A = q.poll(); s.add(A.point); if
	 * (A.getPoint().getValue() == B.getPoint().getValue() &&
	 * A.getPoint().getCol() == B.getPoint().getCol() && A.getPoint().getRow()
	 * == B.getPoint().getRow()) { List<Node> list = new ArrayList<>(); while
	 * (true) { list.add(A); if (A.parent == null) { break; } else { A =
	 * A.parent; } } list.add(new Node(snakeHead)); Collections.reverse(list);
	 * return new Path(list); } else { // A周围4个点，把符合要求的点加入q Point pA =
	 * A.getPoint(); List<Point> list = get4Point(array, pA);
	 * 
	 * list.forEach(n -> { int value = n.getValue(); if (value != Head.value &&
	 * value != Body.value && value != Wall.value && !s.contains(n)) { q.add(new
	 * Node(n, A)); } });
	 * 
	 * } } return new Path(null);
	 * 
	 * }
	 */

	/*
	 * private static List<Point> tempList; private static List<Path> pathList;
	 */
	/**
	 * 两点间的最远距离
	 * 
	 * @param snakeHead
	 * @param snakeTail
	 * @param array
	 * @return
	 */
	/*
	 * public static Path findFarTailPath(Point snakeHead, Tail snakeTail,
	 * Point[][] array) { tempList = new ArrayList<>(); pathList = new
	 * ArrayList<>();
	 * 
	 * List<Point> list = get4Point(array, snakeHead); list.forEach(p -> { int
	 * value = p.getValue(); if (value != Head.value && value != Body.value &&
	 * value != Wall.value) { tempList.add(p); } }); tempList.forEach(p -> {
	 * pathList.add(findTailShortPath(p, snakeTail, array)); });
	 * 
	 * // 由小到大排序 Collections.sort(pathList, (o1, o2) -> { if (o1.getSize() >
	 * o2.getSize()) { return 1; } else if (o1.getSize() == o2.getSize()) {
	 * return 0; } else { return -1; } }); return pathList.get(pathList.size() -
	 * 1); }
	 */

	/*
	 * private static Path tempPath; private static Path otherPath;//
	 */ /*
		 * public static Path findFarBodyPath(Point snakeHead, List<Point>
		 * bodys, Point[][] array) throws Exception { tempPath = new Path(null);
		 * otherPath= new Path(null); tempList = new ArrayList<>(); pathList =
		 * new ArrayList<>();
		 * 
		 * for (int i = 0; i < bodys.size(); i++) { Point body = bodys.get(i);
		 * tempPath = findShortBodyPath(snakeHead, body, array); if
		 * (tempPath.hasPath()) {// 蛇头找的到蛇身的最短距离 List<Point> list =
		 * get4Point(array, tempPath.list.get(0).point); list.forEach(p -> { int
		 * value = p.getValue(); if (value != Head.value && value != Body.value
		 * && value != Wall.value && value != Tail.value) { tempList.add(p); }
		 * }); tempList.forEach(p -> { pathList.add(findShortBodyPath(p, body,
		 * array)); });
		 * 
		 * // 由小到大排序 Collections.sort(pathList, (o1, o2) -> { if (o1.getSize() >
		 * o2.getSize()) { return 1; } else if (o1.getSize() == o2.getSize()) {
		 * return 0; } else { return -1; } });
		 * if(pathList.size()==0){//离蛇尾最近的蛇身没有 路可以走，换离蛇尾次远的蛇身找路径 continue;
		 * }else{//找到可以走的路 Path path_var = pathList.get(pathList.size() - 1);
		 * if(path_var.getSize()<=2){//走一步就找到蛇身 System.out.println("蛇头离蛇身一步距离");
		 * otherPath= path_var; continue; }else{ return path_var; } } } }
		 * if(otherPath.hasPath()){ return otherPath; }else{ throw new
		 * Exception("游戏结束"); } }
		 */

	/*
	 * private static Path findShortBodyPath(Point snakeHead, Point p, Point[][]
	 * array) { q = new LinkedBlockingQueue<>();// 没访问的点 s = new HashSet<>();//
	 * 已经访问的点 A = new Node(snakeHead); B = new Node(p); q.add(A);// 初始化，第一个点
	 * 
	 * while (!q.isEmpty()) { A = q.poll(); s.add(A.point); if (isNear(A.point,
	 * B.point)) { List<Node> list = new ArrayList<>(); while (true) {
	 * list.add(A); if (A.parent == null) { break; } else { A = A.parent; } }
	 * list.add(new Node(snakeHead)); Collections.reverse(list); return new
	 * Path(list); } else { // A周围4个点，把符合要求的点加入q Point pA = A.getPoint();
	 * List<Point> list = get4Point(array, pA);
	 * 
	 * list.forEach(n -> { int value = n.getValue(); if (value != Head.value &&
	 * value != Body.value && value != Tail.value && value != Wall.value &&
	 * !s.contains(n)) { q.add(new Node(n, A)); } });
	 * 
	 * } } return new Path(null); }
	 * 
	 * 
	 * 
	 * public static Path findFarApplePath(Point snakeHead, Point apple,
	 * Point[][] array) { tempList = new ArrayList<>(); pathList = new
	 * ArrayList<>();
	 * 
	 * List<Point> list = get4Point(array, snakeHead); list.forEach(p -> { int
	 * value = p.getValue(); if (value != Head.value && value != Body.value &&
	 * value != Wall.value && value != Tail.value) { tempList.add(p); } });
	 * tempList.forEach(p -> { pathList.add(findAppleShortPath(p, apple,
	 * array)); });
	 * 
	 * // 由小到大排序 Collections.sort(pathList, (o1, o2) -> { if (o1.getSize() >
	 * o2.getSize()) { return 1; } else if (o1.getSize() == o2.getSize()) {
	 * return 0; } else { return -1; } }); return pathList.get(pathList.size() -
	 * 1); }
	 */
}
