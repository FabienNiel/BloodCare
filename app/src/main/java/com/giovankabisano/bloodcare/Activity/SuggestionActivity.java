package com.giovankabisano.bloodcare.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.giovankabisano.bloodcare.R;

public class SuggestionActivity extends AppCompatActivity {

    int SP;
    TextView tv_judul;
    TextView tv_judulSaran1, tv_judulSaran2, tv_judulSaran3;
    TextView tv_deskripsiSaran1, tv_deskripsiSaran2, tv_deskripsiSaran3;
    ImageView im_gambar1, im_gambar2, im_gambar3, banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        tv_judul = findViewById(R.id.tv_judul);
        tv_judulSaran1 = findViewById(R.id.judul_saran1);
        tv_judulSaran2 = findViewById(R.id.judul_saran2);
        tv_judulSaran3 = findViewById(R.id.judul_saran3);
        tv_deskripsiSaran1 = findViewById(R.id.deskripsi_saran1);
        tv_deskripsiSaran2 = findViewById(R.id.deskripsi_saran2);
        tv_deskripsiSaran3 = findViewById(R.id.deskripsi_saran3);
        im_gambar1 = findViewById(R.id.image_saran1);
        im_gambar2 = findViewById(R.id.image_saran2);
        im_gambar3 = findViewById(R.id.image_saran3);
        banner = findViewById(R.id.banner);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SP = bundle.getInt("SP");
        }
        if (SP > 135) {
            showInfoHighBP();
        } else if (SP < 120) {
            showInfoLowBP();
        } else if (SP > 120 && SP < 135) {
            showInfoNormalBP();
        }
    }

    private void showInfoNormalBP() {
        tv_judul.setText("Tekanan Darah Normal");
    }

    private void showInfoLowBP() {
        banner.setImageResource(R.drawable.banner_low1);
        tv_judul.setText("Tekanan Darah Dibawah Normal");
        tv_judulSaran1.setText("Olahraga");
        im_gambar1.setImageResource(R.drawable.olahraga);
        tv_deskripsiSaran1.setText("Aktifitas pergerakan tubuh diperlukan untuk meningkatkan kinerja jantung. Aktifitas yang disarankan adalah olahraga kecil selama 30 menit setidaknya 3 kali dalam seminggu");

        tv_judulSaran2.setText("Asupan kafein");
        tv_deskripsiSaran2.setText("Kopi bagi penderita darah rendah sangat bermanfaat untuk meningkatkan tekanan darah sehingga tekanan darah berada dalam kondisi normal.");
        im_gambar2.setImageResource(R.drawable.coffe);

        tv_judulSaran3.setText("Makanan dengan kadar garam serta karbohidrat");
        im_gambar3.setImageResource(R.drawable.food);
        tv_deskripsiSaran3.setText("Garam dapat meningkatkan tekanan darah. Konsumsi garam yang cukup adalah 5 - 8 gram per hari");
    }

    private void showInfoHighBP() {
        banner.setImageResource(R.drawable.banner_high1);
        tv_judul.setText("Tekanan Darah Diatas Normal");
        tv_judulSaran1.setText("Beristirahat");
        im_gambar1.setImageResource(R.drawable.sleep);
        tv_deskripsiSaran1.setText("Pastikan anda memiliki waktu istirahat yang cukup. Minimal waktu istirahat dalam satu hari adalah 6 jam");

        tv_judulSaran2.setText("Minum cukup air");
        im_gambar2.setImageResource(R.drawable.water);
        tv_deskripsiSaran2.setText("Jumlah air putih yang disarankan adalah sejumlah 2,6 liter perhari atau sekitar 10 gelas. Namun jumlahnya perlu disesuaikan dengan kegiatan yang dilakukan");

        tv_judulSaran3.setText("Konsumsi makanan rendah lemak");
        im_gambar3.setImageResource(R.drawable.food2);
        tv_deskripsiSaran3.setText("Konsumsi lemak yang berlebih juga dapat mengakibatkan tekanan darah tinggi. Anda disarankan agar mengurangi makanan berlemak dan lebih banyak mengonsumsi sayuran");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SuggestionActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
