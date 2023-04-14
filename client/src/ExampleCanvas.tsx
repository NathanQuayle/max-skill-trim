import { useEffect, useRef, useState } from 'react'
import exampleSkillsImage from './example-skills.png'

type Props = {
  file: string
}

export const ExampleCanvas = ({ file }: Props) => {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const [exampleSkillsRendered, setExampleSkillsRendered] = useState(false)

  useEffect(() => {
    if (canvasRef.current) {
      const context = canvasRef.current.getContext('2d')
      if (!context) return
      const scale = 2
      canvasRef.current.height = 256 * scale
      canvasRef.current.width = 188 * scale

      context.imageSmoothingEnabled = false
      context.scale(2, 2)

      const exampleSkills = new Image()
      exampleSkills.src = exampleSkillsImage
      exampleSkills.onload = () => {
        context?.drawImage(exampleSkills, 0, 0)
        setExampleSkillsRendered(true)
      }
    }
  }, [file])

  useEffect(() => {
    if (!exampleSkillsRendered || !canvasRef.current) return
    const context = canvasRef.current.getContext('2d')

    const image = new Image(64, 32)
    image.src = file
    image.onload = () => {
      // HP
      context?.drawImage(image, 63, 0)
      // Range
      context?.drawImage(image, 0, 96)
      // Prayer
      context?.drawImage(image, 0, 128)
      // Slayer
      context?.drawImage(image, 63, 192)
    }
  }, [exampleSkillsRendered, file])

  return <canvas ref={canvasRef} />
}
