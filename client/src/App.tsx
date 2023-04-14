import './App.css'
import { Button, Card, Container, Grid, MantineProvider, Space, Text, Title } from '@mantine/core'
import { assets } from './files'
import { ExampleCanvas } from './ExampleCanvas'

function App() {
  return (
    <MantineProvider
      theme={{ colorScheme: 'dark' }}
      withGlobalStyles
      withNormalizeCSS
    >
      <Title
        order={1}
        align='center'
      >
        Custom Trims
      </Title>

      <Container size='xl'>
        <Space h='lg' />
        <Grid
          gutter='lg'
          justify='center'
        >
          {assets
            .sort(() => 0.5 - Math.random())
            .map((file) => {
              return (
                <Grid.Col span='content'>
                  <Card
                    shadow='md'
                    padding='md'
                    withBorder
                  >
                    <Card.Section variant='d'>
                      <ExampleCanvas file={file.trim} />
                    </Card.Section>
                    <Text tt='capitalize'>{file.fileName?.split('-').join(' ').slice(0, -4)}</Text>
                    <Text c='dimmed'>By {file.designer}</Text>
                    <Space h='sm' />
                    <Button
                      fullWidth
                      component='a'
                      download={file.fileName || 'trim'}
                      href={file.trim}
                    >
                      Download
                    </Button>
                  </Card>
                </Grid.Col>
              )
            })}
        </Grid>
      </Container>
    </MantineProvider>
  )
}

export default App
