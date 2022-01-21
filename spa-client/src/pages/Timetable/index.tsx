import { TimetableLayout, Days, Time } from "./styles";
import { SectionHeading } from "../../components/generalStyles/utils";
import React from "react";

function Timetable() {
  const days: string[] = [
    "Lunes",
    "Martes",
    "Miercoles",
    "Jueves",
    "Viernes",
    "Sabado",
  ];
  const hours: string[] = [
    "08:00",
    "09:00",
    "10:00",
    "11:00",
    "12:00",
    "13:00",
    "14:00",
    "15:00",
    "16:00",
    "17:00",
    "18:00",
    "19:00",
    "20:00",
    "21:00",
    "22:00",
  ];
  const colors: string[] = [
    "#2EC4B6",
    "#173E5C",
    "#B52F18",
    "#821479",
    "#6F9A13",
  ];

  return (
    <>
      <SectionHeading>Horarios</SectionHeading>
      <div>
        <TimetableLayout>
          <Days>
            <th></th>
            {days.map((day) => (
              <th key={day}>{day}</th>
            ))}
          </Days>
          {hours.map((hour) => (
            <tr key={hour}>
              <Time>{hour}</Time>
              {
                //FALTA LOGICA
                days.map((day) => (
                  <td key={day}></td>
                ))
              }
            </tr>
          ))}
        </TimetableLayout>
      </div>
    </>
  );
}

export default Timetable;
