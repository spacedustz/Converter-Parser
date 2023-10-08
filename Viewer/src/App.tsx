import './App.css'

import styled, {css} from "styled-components";
import StreamingRtsp from "./components/StreamingRtsp";
import React from "react";

const AppBlock = styled.div`
  width: 512px;
  margin: 0 auto;
  margin-top: 4rem;
  border: 1px solid black;
  padding: 1rem;
`;

const App: React.FC = () => {

    return (
        <div>
            <AppBlock>
                <StreamingRtsp/>
            </AppBlock>
        </div>
    )
}

export default App
