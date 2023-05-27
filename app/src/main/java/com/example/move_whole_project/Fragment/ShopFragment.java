package com.example.move_whole_project.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.move_whole_project.R;

public class ShopFragment extends Fragment {
    private Button buyButton1, buyButton2, buyButton3, buyButton4;
    private TextView pointsTextView;
    private int currentPoints = 10000; // 현재 포인트

    // 상점 아이템 리스트
    private ShopItem shopItem1;
    private ShopItem shopItem2;
    private ShopItem shopItem3;
    private ShopItem shopItem4;

    // 상점 아이템 구매 내역 저장
    private static final String PREFS_NAME = "ShopPrefs";
    private static final String SHOP_KEY_ITEM1 = "shopItem1";
    private static final String SHOP_KEY_ITEM2 = "shopItem2";
    private static final String SHOP_KEY_ITEM3 = "shopItem3";
    private static final String SHOP_KEY_ITEM4 = "shopItem4";

    // 화면 넘어가기 버튼
    Button shopApplyButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // 상점 아이템 초기화
        shopItem1 = new ShopItem("모자", 500);
        shopItem2 = new ShopItem("가방", 700);
        shopItem3 = new ShopItem("대나무헬리콥터", 900);
        shopItem4 = new ShopItem("크리스마스트리", 1200);


        buyButton1 = view.findViewById(R.id.buy_button1);
        buyButton2 = view.findViewById(R.id.buy_button2);
        buyButton3 = view.findViewById(R.id.buy_button3);
        buyButton4 = view.findViewById(R.id.buy_button4);

        pointsTextView = view.findViewById(R.id.point_text_input);
        pointsTextView.setText(String.valueOf(currentPoints));

        // 구매 내역을 SharedPreferences에서 읽어와 각 아이템의 상태를 설정
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        shopItem1.setPurchased(sharedPreferences.getBoolean(SHOP_KEY_ITEM1, false));
        shopItem2.setPurchased(sharedPreferences.getBoolean(SHOP_KEY_ITEM2, false));
        shopItem3.setPurchased(sharedPreferences.getBoolean(SHOP_KEY_ITEM3, false));
        shopItem4.setPurchased(sharedPreferences.getBoolean(SHOP_KEY_ITEM4, false));

        // 각 아이템의 상태에 따라 버튼 텍스트와 활성화 여부 설정
        updateButtonState(buyButton1, shopItem1);
        updateButtonState(buyButton2, shopItem2);
        updateButtonState(buyButton3, shopItem3);
        updateButtonState(buyButton4, shopItem4);
    
        // 버튼 리스너
        buyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 물품1 구매 처리
                purchaseItem(shopItem1, buyButton1);
            }
        });

        buyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 물품2 구매 처리
                purchaseItem(shopItem2, buyButton2);
            }
        });

        buyButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 물품3 구매 처리
                purchaseItem(shopItem3, buyButton3);
            }
        });

        buyButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 물품4 구매 처리
                purchaseItem(shopItem4, buyButton4);
            }
        });

        // 구매한거 적용 클래스
        shopApplyButton = view.findViewById(R.id.apply_button);
        shopApplyButton.setOnClickListener(new View.OnClickListener() {
            class ShopApplyActivity {
            }

            @Override
            public void onClick(View v) {
                // "적용" 버튼 클릭 시 activity_shop_apply.xml로 전환
                Intent intent = new Intent(requireContext(), ShopApplyActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    // 아이템에 대한 SharedPreferences 키 값을 반환(키 값 저장하고 아바타 적용을 위해서 만든거)
    private String getItemKey(ShopItem item) {
        switch (item.getName()) {
            case "모자":
                return SHOP_KEY_ITEM1;
            case "가방":
                return SHOP_KEY_ITEM2;
            case "대나무헬리콥터":
                return SHOP_KEY_ITEM3;
            case "크리스마스트리":
                return SHOP_KEY_ITEM4;
            default:
                return "";
        }
    }

    // 버튼 상태 업데이트
    private void updateButtonState(Button button, ShopItem item) {
        if (item.isPurchased()) {
            button.setText("구매 완료");
            button.setEnabled(false);
        } else {
            button.setText("구매하기");
            button.setEnabled(true);
        }
    }

    // 아이템 구매 처리
    private void purchaseItem(ShopItem item, Button button) {
        if (!item.isPurchased()) {
            int itemPrice = item.getPrice();

            if (currentPoints >= itemPrice) {
                currentPoints -= itemPrice;
                pointsTextView.setText(String.valueOf(currentPoints));
                Toast.makeText(getContext(), item.getName() + "을(를) 구매하였습니다. 현재 포인트: " + currentPoints, Toast.LENGTH_SHORT).show();

                item.setPurchased(true);
                button.setText("구매 완료");
                button.setEnabled(false);

                // 아이템 구매 상태를 SharedPreferences에 저장
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getItemKey(item), true);
                editor.apply();
            } else {
                Toast.makeText(getContext(), "포인트가 부족합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 상점에 대한 getter, setter
    public class ShopItem {
        private String name;
        private int price;
        private boolean purchased;

        public ShopItem(String name, int price) {
            this.name = name;
            this.price = price;
            this.purchased = false;
        }

        public String getName() { // 물품 이름
            return name;
        }

        public int getPrice() {  // 물품 가격
            return price;
        }

        public boolean isPurchased() { // 구매 여부
            return purchased;
        }

        public void setPurchased(boolean purchased) {
            this.purchased = purchased;
        }
    }
}