package kazarovets.flatspinger.utils

import android.location.Location
import kazarovets.flatspinger.model.Subway


class SubwayUtils {

    companion object {

        val KAMENNAYA_GORKA_ID = 223
        val KUNCEVSHINA_ID = 222
        val SPORTIVNAYA_ID = 221
        val PUSHKINSKAYA_ID = 220
        val MOLODEZHNAYA_ID = 219
        val FRUNZENSKAYA_ID = 218
        val NEMIGA_ID = 217
        val KUPALOVSKAYA_ID = 216
        val PERVOMAYSKAYA_ID = 215
        val PROLETARSKAYA_ID = 214
        val TRAKTORNIY_ZAVOD_ID = 213
        val PARTIZANSKAYA_ID = 212
        val AVTOZAVODSKAYA_ID = 211
        val MOGILEVSKAYA_ID = 210

        val MALINOVKA_ID = 110
        val PETROVSHINA_ID = 111
        val MIHALOVA_ID = 112
        val GRUSHEVKA_ID = 113
        val INSTITUT_KULTURY_ID = 114
        val PLOSHAD_LENINA_ID = 115
        val OKTYABRSKAYA_ID = 116
        val PLOSHAD_POBEDY_ID = 117
        val PLOSHAD_YAKUBA_KOLASA_ID = 118
        val AKADEMIYA_NAUK_ID = 119
        val PARK_CHELUSKINCEV_ID = 120
        val MOSKOVSKAYA_ID = 121
        val VOSTOK_ID = 122
        val BORISOVSKII_TRAKT_ID = 123
        val URUCIE_ID = 124

        val RED_LINE_SUBWAYS = arrayOf(
                Subway("Каменная Горка", 53.906876, 27.437716, 223),
                Subway("Кунцевщина", 53.906537, 27.454189, 222),
                Subway("Спортивная", 53.908491, 27.479950, 221),
                Subway("Пушкинская", 53.911602, 27.497027, 220),
                Subway("Молодёжная", 53.906348, 27.522640, 219),
                Subway("Фрунзенская", 53.905283, 27.539184, 218),
                Subway("Немига", 53.905694, 27.553961, 217),
                Subway("Купаловская", 53.901373, 27.560995, 216),
                Subway("Первомайская", 53.893844, 27.570516, 215),
                Subway("Пролетарская", 53.889634, 27.585608, 214),
                Subway("Тракторный Завод", 53.889229, 27.614962, 213),
                Subway("Партизанская", 53.875174, 27.629542, 212),
                Subway("Автозаводская", 53.869949, 27.647811, 211),
                Subway("Могилёвская", 53.861592, 27.674590, 210))


        val BLUE_LINE_SUBWAYS = arrayOf(
                Subway("Малиновка", 53.849452, 27.474970, 110),
                Subway("Петровщина", 53.864060, 27.485406, 111),
                Subway("Михалово", 53.876948, 27.497084, 112),
                Subway("Грушевка", 53.886668, 27.514765, 113),
                Subway("Институт Культуры", 53.885253, 27.539492, 114),
                Subway("Пл. Ленина", 53.894745, 27.548239, 115),
                Subway("Октябрьская", 53.902317, 27.563073, 116),
                Subway("Пл. Победы", 53.908145, 27.575434, 117),
                Subway("Пл. Якуба Коласа", 53.915417, 27.583003, 118),
                Subway("Академия Наук", 53.922117, 27.600508, 119),
                Subway("Парк Челюскинцев", 53.923713, 27.612221, 120),
                Subway("Московская", 53.927965, 27.627772, 121),
                Subway("Восток", 53.934469, 27.651277, 122),
                Subway("Борисовский Тракт", 53.938652, 27.665355, 123),
                Subway("Уручье", 53.945352, 27.687875, 124))

        fun getNearestSubway(latitude: Double, longitude: Double) : Subway {
            var min = Double.MAX_VALUE
            var nearest: Subway = RED_LINE_SUBWAYS[0]
            for (subway in RED_LINE_SUBWAYS + BLUE_LINE_SUBWAYS) {
                val distance = distanceBetweenInMeters(subway.latitude, subway.longitude, latitude, longitude)
                if (distance < min) {
                    min = distance
                    nearest = subway
                }
            }
            return nearest

        }

        fun distanceBetweenInMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val locationA = Location("point A")
            locationA.setLatitude(lat1)
            locationA.setLongitude(lng1)

            val locationB = Location("point B")
            locationB.setLatitude(lat2)
            locationB.setLongitude(lng2)

            return locationA.distanceTo(locationB).toDouble()
        }
    }

}