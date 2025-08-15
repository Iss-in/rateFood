import { useState } from "react";
import { Star } from "lucide-react";
import { Button } from "./ui/button";

interface RatingComponentProps {
  currentRating?: number;
  onRatingChange: (rating: number) => void;
  readonly?: boolean;
  size?: "sm" | "md" | "lg";
}

export function RatingComponent({ 
  currentRating = 0, 
  onRatingChange, 
  readonly = false,
  size = "md" 
}: RatingComponentProps) {
  const [hoverRating, setHoverRating] = useState(0);
  
  const sizeClasses = {
    sm: "h-4 w-4",
    md: "h-5 w-5", 
    lg: "h-6 w-6"
  };

  const handleClick = (rating: number) => {
    if (!readonly) {
      onRatingChange(rating);
    }
  };

  const handleMouseEnter = (rating: number) => {
    if (!readonly) {
      setHoverRating(rating);
    }
  };

  const handleMouseLeave = () => {
    if (!readonly) {
      setHoverRating(0);
    }
  };

  return (
    <div className="flex items-center space-x-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          className={`${sizeClasses[size]} cursor-pointer transition-colors ${
            star <= (hoverRating || currentRating)
              ? "fill-yellow-400 text-yellow-400"
              : "text-gray-300"
          } ${readonly ? "cursor-default" : ""}`}
          onClick={() => handleClick(star)}
          onMouseEnter={() => handleMouseEnter(star)}
          onMouseLeave={handleMouseLeave}
        />
      ))}
      {currentRating > 0 && (
        <span className="ml-2 text-sm text-muted-foreground">
          ({currentRating}/5)
        </span>
      )}
    </div>
  );
}