package kazarovets.flatspinger.utils

import android.location.Location
import kazarovets.flatspinger.model.Subway


class SubwayUtils {

    companion object {
        val RED_LINE_SUBWAYS = arrayOf(
                Subway("Каменная Горка", 53.906876, 27.437716),
                Subway("Кунцевщина", 53.906537, 27.454189),
                Subway("Спортивная", 53.908491, 27.479950),
                Subway("Пушкинская", 53.911602, 27.497027),
                Subway("Молодёжная", 53.906348, 27.522640),
                Subway("Фрунзенская", 53.905283, 27.539184),
                Subway("Немига", 53.905694, 27.553961),
                Subway("Купаловская", 53.901373, 27.560995),
                Subway("Первомайская", 53.893844, 27.570516),
                Subway("Пролетарская", 53.889634, 27.585608),
                Subway("Тракторный Завод", 53.889229, 27.614962),
                Subway("Партизанская", 53.875174, 27.629542),
                Subway("Автозаводская", 53.869949, 27.647811),
                Subway("Могилёвская", 53.861592, 27.674590))


        val BLUE_LINE_SUBWAYS = arrayOf(
                Subway("Малиновка", 53.849452, 27.474970),
                Subway("Петровщина", 53.864060, 27.485406),
                Subway("Михалово", 53.876948, 27.497084),
                Subway("Грушевка", 53.886668, 27.514765),
                Subway("Институт Культуры", 53.885253, 27.539492),
                Subway("Пл. Ленина", 53.894745, 27.548239),
                Subway("Октябрьская", 53.902317, 27.563073),
                Subway("Пл. Победы", 53.908145, 27.575434),
                Subway("Пл. Якуба Коласа", 53.915417, 27.583003),
                Subway("Академия Наук", 53.922117, 27.600508),
                Subway("Парк Челюскинцев", 53.923713, 27.612221),
                Subway("Московская", 53.927965, 27.627772),
                Subway("Восток", 53.934469, 27.651277),
                Subway("Борисовский Тракт", 53.938652, 27.665355),
                Subway("Уручье", 53.945352, 27.687875))

        fun getNearestSubway(latitude: Double, longitude: Double) : Subway {
            var min = Double.MAX_VALUE
            var nearest: Subway = RED_LINE_SUBWAYS[0]
            for (subway in RED_LINE_SUBWAYS + BLUE_LINE_SUBWAYS) {
                val distance = distanceBetween(subway.latitude, subway.longitude, latitude, longitude)
                if (distance < min) {
                    min = distance
                    nearest = subway
                }
            }
            return nearest

        }

        private fun distanceBetween(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
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