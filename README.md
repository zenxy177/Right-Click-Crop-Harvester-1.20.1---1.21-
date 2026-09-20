# 🌾 Sağ Tık Hasat (Right-Click Crop Harvester)

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1%20--%201.21%2B-brightgreen.svg)](https://papermc.io)
[![API](https://img.shields.io/badge/Paper-API-blue.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-17%20%2F%2021-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Oyuncuların olgunlaşmış ekinlere sağ tıklayarak pratik bir şekilde otomatik toplayabilmesini ve yerlerine yenilerini ekebilmesini sağlayan gelişmiş Minecraft Paper & Spigot eklentisi.

---

## ✨ Özellikler

- **Geniş Sürüm Desteği:** Minecraft 1.20.1, 1.20.4, 1.20.6 ve 1.21+ (Paper, Purpur, Spigot) ile %100 uyumlu.
- **Desteklenen Ekinler:** Buğday (`WHEAT`), Havuç (`CARROTS`), Patates (`POTATOES`), Pancar (`BEETROOTS`), Nether Yumrusu (`NETHER_WART`) ve Kakao Çekirdeği (`COCOA`).
- **Çapa Zorunluluğu & Dayanıklılık:** Hasat için çapa kullanımı zorunludur. Her hasatta çapanın canı düşer (Kırılmazlık / Unbreaking büyüsü desteklenmektedir).
- **Servet (Fortune) Desteği:** Elinizdeki çapadaki Servet büyüsü düşen ürün miktarını artırır.
- **Tohum Tüketim Dengesi:** Ekinin yeniden ekilmesi için düşen üründen veya envanterden 1 tohum harcanır. Tohum yoksa ekim yapılmaz ve oyuncu uyarılır.
- **Kemik Tozu & Makro Engeli:** Kemik Tozu ile yapılan sağ tık makroları engellenmiştir.
- **Efekt & Sesler:** Hasat anında yeşil parçacıklar ve kırma sesleri oynatılır.
- **Türkçe Konfigürasyon:** Tamamen özelleştirilebilir `config.yml`.

---

## 💻 Komutlar ve İzinler

| Komut | Açıklama | Yetki (Permission) |
|---|---|---|
| `/sagtikhasat reload` | Eklenti konfigürasyonunu yeniden yükler. | `sagtikhasat.admin` |
| `/sth` veya `/rcharvest` | Sağ tık hasat bilgilendirme mesajını gösterir. | `sagtikhasat.use` |

* **Kullanım Yetkisi:** `sagtikhasat.use` (Varsayılan: Tüm oyuncular)
* **Yönetici Yetkisi:** `sagtikhasat.admin` (Varsayılan: OP)

---

## ⚙️ Yapılandırma (`config.yml`)

```yaml
enabled-crops:
  WHEAT: true
  CARROTS: true
  POTATOES: true
  BEETROOTS: true
  NETHER_WART: true
  COCOA: true

require-hoe: true
damage-hoe: true
enable-fortune: true
auto-inventory: false
consume-seed: true
notify-unripe: true

effects:
  play-sound: true
  sound-name: "BLOCK_CROP_BREAK"
  play-particles: true
  particle-name: "VILLAGER_HAPPY"
```

---

## 🛠️ Projeyi Derleme (Build)

Projeyi bilgisayarınızda derlemek için Maven ve Java 17/21 gereklidir:

```bash
git clone https://github.com/zenxy177/Sa-T-k-Hasat---Right-Click-Crop-Harvester-1.20.1---1.21-.git
cd SagTikHasat
mvn clean package
```

Derlenen `.jar` dosyası `target/SagTikHasat-1.0.0.jar` dizininde oluşacaktır.

---

## 📜 Lisans

Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.
