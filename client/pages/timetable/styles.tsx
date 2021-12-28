import styled from "styled-components";
import Link from "next/link";

export const SectionHeading = styled.h2`
  color: ${(props) => props.theme.cyanDarkest};
  font-size: 24px;
  margin-bottom: 20px;
`;

export const TimetableLayout = styled.table`
  color: #efefef;
  border-collapse: collapse;
  margin-bottom: 50px;
  th,
  td {
    padding: 1em;
    width: 8em;
    border: 1px solid black;
  }
`;

export const Days = styled.tr`
  background: ${(props) => props.theme.cyanDarkest};
  text-transform: uppercase;
  font-size: 1em;
  text-align: center;
`;
export const Time = styled.td`
  background: ${(props) => props.theme.cyanDarkest};
  text-transform: uppercase;
  font-size: 1em;
  text-align: center;
  width: 3em !important;
`;
