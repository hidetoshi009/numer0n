package server;

import java.util.HashSet;
import java.util.Set;

public class JUDGE {
	Server server;
	int eat = 0;
	int bite = 0;
	// boolean myturn;
	// boolean tekiturn;

	public boolean isNumber(String num) {// 入力が3桁の数値であるかどうか判定
		try {
			Integer.parseInt(num);
			String[] strArray = num.split("");
			Set<String> set = new HashSet<String>();
			set.add(strArray[0]);
			set.add(strArray[1]);
			set.add(strArray[2]);
			if (num.length() == 3 && set.size() == 3) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("@isNumber入力ミス" + e);
			return false;
		}

	}

	public JUDGE EatBite(String predict, String tekinum) {// 予測と敵の数値のeat,bite情報を返す
		JUDGE eatbite = new JUDGE();
		String[] predictArray = predict.split("");
		String[] tekinumArray = tekinum.split("");

		for (int i = 0; i < predictArray.length; i++) {
			for (int j = 0; j < predictArray.length; j++) {
				if (predictArray[i].equals(tekinumArray[j])) { // ==だとだめ
					if (i == j) { // eat
						eatbite.eat++;

					} else { // bite
						eatbite.bite++;
					}
				} else {
					// 数値が違う
				}
			}
		}
		// System.out.println("eat: "+String.valueOf(eatbite.eat));
		// System.out.println("bite: "+String.valueOf(eatbite.bite));

		return eatbite;
	}

	public boolean Finish(int eat) {// 3eatになったら勝負あり
		if (eat == 3) {
			return true;
		} else {
			return false;
		}
	}

}